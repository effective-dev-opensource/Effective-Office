package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import ru.auroraos.kmp.keyboard.maliit.Keyboard

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
 * The fork reports no keyboard insets, so the height is taken from the maliit session itself —
 * asked for, not listened to.
 *
 * `Keyboard.listenState` does fire when the keyboard opens, but the state event carries
 * `height = 0`: maliit sends the size in a follow-up event, and that one never reaches the app.
 * `Keyboard.height()` answers correctly at any moment, so it is polled instead. Polling also
 * happens to be the sturdier of the two here — there is no subscription to lose, and the fork
 * drops its listeners on `onWindowPause()` without ever restoring them.
 *
 * The poll only runs while whoever reads this is on screen, which is the modal host, so nothing
 * ticks on the main screen. It stays on the composition's dispatcher (the main thread) on purpose:
 * the maliit binding is a Qt object and Qt objects are bound to the thread they were made on.
 *
 * Two things fall out of asking rather than being told, both wanted: the keyboard slides in over
 * several frames and the answer grows with it, so the modal follows it up rather than jumping; and
 * a keyboard swiped away behind the app's back — a real gesture on Aurora, and one the fork does
 * not report — shows up as a height of zero on the next tick and the modal comes back down.
 *
 * The height is asked for unconditionally; `isOpen()` only goes into the log beside it. Gating the
 * question on that flag is what this used to do, and on the tablet it produced silence — not one
 * overlap line while the keyboard was plainly on screen and typing into the field. The same flag
 * already lied once, in [closeSoftKeyboard], where it read false with the keyboard still up. It
 * describes something narrower than "there is a keyboard", so nothing is decided by it any more;
 * the two values side by side in the log are what will say what it actually tracks on that device.
 */
@Composable
actual fun softKeyboardOverlapPx(): Int {
    val overlap by produceState(0) {
        while (true) {
            // The binding is the fork's, and the fork disposes it from `onWindowPause()` without
            // telling anyone, so a call landing after that is unmapped ground. A keyboard that
            // cannot be measured is not worth an app, and it is not worth a log line every 100 ms
            // either — say it once and stop asking.
            val reading = runCatching {
                Keyboard.height().toInt() to Keyboard.isOpen()
            }.getOrElse {
                Napier.e(throwable = it, tag = KEYBOARD_TAG) { "height poll failed, giving up" }
                return@produceState
            }
            val (height, isOpen) = reading
            if (height != value) {
                Napier.i(tag = KEYBOARD_TAG) { "overlap ${value}px -> ${height}px (isOpen=$isOpen)" }
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
