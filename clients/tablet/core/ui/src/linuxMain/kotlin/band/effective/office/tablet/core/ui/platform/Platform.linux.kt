package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalWindowInfo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import ru.auroraos.kmp.keyboard.maliit.Keyboard
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

actual val forceLandscape: Boolean = true

actual val popupIsSeparateScene: Boolean = true

/**
 * Parity with the reference Android tablet, and it happens to be exact.
 *
 * Both devices are 1200 px on the short side — the Quadro T's window is 1200x2000 and the Android
 * reference is 1920x1200 — so a baseline of 686 dp substitutes a density of 1200/686 = 1.7493
 * against Android's own 1.75. The two lay out the same, not merely close.
 */
actual val uiScaleBaseline: Dp = 686.dp

/** How often the maliit session is asked how tall its keyboard is. */
private const val KEYBOARD_POLL_MS = 100L

/**
 * Tag for everything said about the keyboard, so a run can be read back off the device with
 * `journalctl --no-pager | grep -E 'SoftKeyboard|OrganizerPicker'`.
 */
private const val KEYBOARD_TAG = "SoftKeyboard"

/**
 * How much of the window's short side the keyboard is assumed to cover while it is open.
 *
 * An estimate, because the real number is not exposed: maliit's surface spans the whole screen in
 * the panel's native portrait frame, so `Keyboard.height()` returns the full long side (2000 on
 * the Quadro T), and the key strip's thickness exists nowhere in the binding, down to the libac
 * struct (`{ height, is_open }`). Half is deliberately the high guess: overshooting only lifts
 * the field further above the keys, undershooting hides it.
 */
private const val KEYBOARD_COVER_FRACTION = 0.5f

/** When the last advance notice of a keyboard arrived. See [noteSoftKeyboardExpected]. */
private var keyboardExpectedAt: TimeSource.Monotonic.ValueTimeMark? = null

/**
 * How long an advance notice is trusted: long enough to bridge the fork's session-start handshake
 * (up to ~2 s seen), short enough that a press that summons no keyboard does not leave the modal
 * hanging mid-air for long.
 */
private val KEYBOARD_EXPECTED_GRACE = 3.seconds

actual fun noteSoftKeyboardExpected() {
    keyboardExpectedAt = TimeSource.Monotonic.markNow()
}

/**
 * The fork reports no keyboard insets, so the maliit session itself is polled — asked for, not
 * listened to. Do not "improve" this into a `Keyboard.listenState` subscription: a second
 * listener next to the fork's own breaks maliit — after the first session closes, no later focus
 * gets a keyboard until the app restarts. It would buy nothing anyway: the state event and the
 * first truthful `isOpen()` land within milliseconds of each other, at the *end* of the fork's
 * session-start handshake — which is also why [noteSoftKeyboardExpected] exists.
 *
 * The poll only runs while whoever reads this is on screen, which is the modal host. It stays on
 * the composition's dispatcher (the main thread) on purpose: the maliit binding is a Qt object
 * and Qt objects are bound to the thread they were made on.
 *
 * An open flag rather than a measured height — see [KEYBOARD_COVER_FRACTION]. The poll is also
 * what notices a keyboard swiped away behind the app's back, a gesture the fork does not report.
 */
@Composable
actual fun softKeyboardOverlapPx(): Int {
    val windowInfo = LocalWindowInfo.current
    val overlap by produceState(0, windowInfo) {
        while (true) {
            // The binding is the fork's, and the fork disposes it from `onWindowPause()` without
            // telling anyone, so a call landing after that is unmapped ground. A keyboard that
            // cannot be measured is not worth an app, and it is not worth a log line every 100 ms
            // either — say it once and stop asking.
            val reallyOpen = runCatching { Keyboard.isOpen() }.getOrElse {
                Napier.e(throwable = it, tag = KEYBOARD_TAG) { "open poll failed, giving up" }
                return@produceState
            }
            // The notice only expires, it is never cleared on a truthful-looking `isOpen()`:
            // that can be a stale `true` from the previous session arriving right after the next
            // press, and acting on it kills the fresh notice before the keyboard moves. Worst
            // case of letting it run out: an open-and-close faster than the grace keeps the modal
            // lifted for the remainder.
            val expected = keyboardExpectedAt?.let { it.elapsedNow() < KEYBOARD_EXPECTED_GRACE }
                ?: false
            val open = reallyOpen || expected
            val height = if (open) {
                val size = windowInfo.containerSize
                (min(size.width, size.height) * KEYBOARD_COVER_FRACTION).roundToInt()
            } else {
                0
            }
            if (height != value) {
                Napier.i(tag = KEYBOARD_TAG) { "overlap ${value}px -> ${height}px" }
                value = height
            }
            delay(KEYBOARD_POLL_MS)
        }
    }
    return overlap
}

/**
 * Close requests wait here for [AuroraKeyboardSessionCloser] to pick them up. Conflated: ten
 * requests before the next frame mean one close, which is all a session needs.
 */
private val keyboardCloseRequests = Channel<Unit>(Channel.CONFLATED)

actual fun closeSoftKeyboard() {
    // Never closes inline: this can be reached from inside maliit's own key dispatch (Done ends
    // editing, editing drops the focus, the focus change lands here synchronously), and
    // Keyboard.close() called from within send_input deadlocks the whole process against the
    // dispatch that is still on the stack — send_state waits on the channel send_input holds.
    // Deferring with a launch does not work either: both of the fork's dispatchers run tasks
    // inline on the main thread, so only a real suspension point leaves the dispatch. The close
    // itself lives in [AuroraKeyboardSessionCloser], on the far side of one.
    keyboardCloseRequests.trySend(Unit)
}

/**
 * The consumer half of [closeSoftKeyboard]: waits a frame, then closes the maliit session.
 *
 * A composable, because the frame clock is the one scheduler on this fork that genuinely defers —
 * and only a composition scope carries one. Mount it once at the root, before any content that
 * edits text.
 */
@Composable
fun AuroraKeyboardSessionCloser() {
    LaunchedEffect(Unit) {
        for (request in keyboardCloseRequests) {
            // The trySend above may resume this coroutine inline, still inside the key dispatch —
            // the frame await is what guarantees the close runs outside it.
            withFrameNanos { }
            runCatching {
                // Unconditionally, and `isOpen()` only goes into the log. That flag says whether the
                // keyboard is on screen, and by the time editing ends it is already false: maliit hides
                // itself the moment the Qt focus goes, a tick before the height even drops. The session
                // behind it is the thing that is never torn down, and closing that is the entire point of
                // this call — guarding it on visibility, as the first version did, meant it never ran once.
                val visible = Keyboard.isOpen()
                Keyboard.close()
                Napier.i(tag = KEYBOARD_TAG) {
                    "closed the session, keyboard was ${if (visible) "up" else "already down"}"
                }
            }.onFailure {
                Napier.e(throwable = it, tag = KEYBOARD_TAG) { "closing the session failed" }
            }
        }
    }
}
