package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import ru.auroraos.kmp.keyboard.maliit.Keyboard
import ru.auroraos.kmp.window.Window
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

actual val forceLandscape: Boolean = true

actual val statusBarInset: Dp = 24.dp

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
 * How much of the screen's short side the keyboard is taken to cover when its own answer cannot
 * be believed — see [readKeyboard].
 *
 * Measured rather than guessed, and twice: off a screenshot of the Quadro T, where the key strip is
 * a band about 520 px wide across a 1200 px short side; and off the emulator, where `height()`
 * happens to answer honestly and says 520 against the same 1200. Two devices, one number — so the
 * estimate is set to what the keyboard turns out to be rather than rounded up away from it.
 */
private const val KEYBOARD_COVER_FRACTION = 0.43f

/** When a field was last pressed, i.e. when a keyboard was last promised. */
private var keyboardExpectedAt: TimeSource.Monotonic.ValueTimeMark? = null

/**
 * How long a press is taken as a promise of a keyboard.
 *
 * Generous, because the handshake it has to cover is: on the Quadro T a press has been seen to
 * produce a keyboard six seconds later, with focus granted and taken away again in between. Three
 * seconds — the fork's own worst case on the phone — expired mid-handshake there, and the modal
 * dropping back at that moment reads to the host as a keyboard that has gone away, which costs the
 * field its focus and the session its life while the keyboard is still on its way up.
 *
 * The promise does not usually live this long: it is dropped the moment a keyboard really shows up
 * ([readKeyboard]) or the field stops being edited ([closeSoftKeyboard]). The timeout is only the
 * backstop for a press that summons nothing at all.
 */
private val KEYBOARD_EXPECTED_GRACE = 10.seconds

/**
 * How long after a press `isOpen()` is not yet taken as proof of a keyboard — long enough for a
 * stale `true` from the previous session to be read and ignored.
 */
private val KEYBOARD_NOTICE_SETTLE = 300.milliseconds

actual fun noteSoftKeyboardExpected() {
    keyboardExpectedAt = TimeSource.Monotonic.markNow()
}

/** One poll's worth of what the keyboard and the screen say, kept together for the log line. */
private data class KeyboardReading(
    val overlap: Int,
    val reported: Int,
    val isOpen: Boolean,
    val screenWidth: Int,
    val screenHeight: Int,
    val estimated: Boolean,
)

/**
 * Asks the keyboard how tall it is, and decides whether the answer means anything.
 *
 * It does on the dev phone: 535 against a 720x1600 screen, a keyboard-shaped number. It does not
 * on the Quadro T, which answers 2000 against 1200x2000 — the screen's whole long side, because
 * maliit's surface spans the screen in its own portrait frame while the key strip's thickness is
 * nowhere in the binding, down to the libac struct (`{ height, is_open }`). Neither is our bug,
 * and both come out of the same call, so the reading is judged by what a keyboard can plausibly
 * be: anything reaching the screen's short side is a surface, not a keyboard, and gets replaced
 * by [KEYBOARD_COVER_FRACTION] of that side.
 *
 * The substitute follows `isOpen()` rather than the reported height, because a number that never
 * meant anything cannot be trusted to fall back to zero either.
 */
private fun readKeyboard(): KeyboardReading {
    val reported = Keyboard.height().toInt()
    val isOpen = Keyboard.isOpen()
    val screenWidth = Window.screenWidth()
    val screenHeight = Window.screenHeight()
    val shortSide = minOf(screenWidth, screenHeight)

    // Nothing to judge against — take the reported number at face value.
    if (shortSide <= 0) {
        return KeyboardReading(
            overlap = maxOf(0, reported),
            reported = reported,
            isOpen = isOpen,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            estimated = false,
        )
    }

    // A press is a promise of a keyboard, and for the second or two the fork takes to start the
    // session it is the only evidence there is — see [noteSoftKeyboardExpected].
    val notice = keyboardExpectedAt
    val expected = notice?.let { it.elapsedNow() < KEYBOARD_EXPECTED_GRACE } == true
    val believable = reported in 1 until shortSide

    // Once the keyboard is really there, the promise has been kept and is dropped. Leaving it to
    // expire on its own instead makes the modal bounce: close the keyboard inside the grace window
    // and the spent promise lifts the card straight back up for the remainder of it.
    //
    // A believable height proves a keyboard outright. `isOpen()` only counts once the notice has
    // had a moment to settle — read immediately after the press it can still be a stale `true`
    // from the previous session, and taking that for proof would spend the promise before the
    // keyboard has moved at all.
    if (notice != null && (believable || (isOpen && notice.elapsedNow() > KEYBOARD_NOTICE_SETTLE))) {
        keyboardExpectedAt = null
    }

    val overlap = when {
        believable -> reported
        isOpen || expected -> (shortSide * KEYBOARD_COVER_FRACTION).roundToInt()
        else -> 0
    }
    return KeyboardReading(
        overlap = overlap,
        reported = reported,
        isOpen = isOpen,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        estimated = !believable && overlap > 0,
    )
}

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
 * What the height means, and when it means nothing, is [readKeyboard]'s business; the log line
 * carries the raw answer and the screen beside the number actually used, so a run off a new device
 * says which of the two it took.
 */
@Composable
actual fun softKeyboardOverlapPx(): Int {
    val overlap by produceState(0) {
        while (true) {
            // The binding is the fork's, and the fork disposes it from `onWindowPause()` without
            // telling anyone, so a call landing after that is unmapped ground. A keyboard that
            // cannot be measured is not worth an app, and it is not worth a log line every 100 ms
            // either — say it once and stop asking.
            val reading = runCatching { readKeyboard() }.getOrElse {
                Napier.e(throwable = it, tag = KEYBOARD_TAG) { "height poll failed, giving up" }
                return@produceState
            }
            if (reading.overlap != value) {
                Napier.i(tag = KEYBOARD_TAG) {
                    "overlap ${value}px -> ${reading.overlap}px (isOpen=${reading.isOpen}, " +
                        "reported=${reading.reported}px, " +
                        "screen ${reading.screenWidth}x${reading.screenHeight}" +
                        (if (reading.estimated) ", estimated)" else ")")
                }
                value = reading.overlap
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
    //
    // Editing is over, so nobody is waiting for a keyboard any more: whatever a press promised is
    // withdrawn here rather than left to time out and lift the modal over an empty screen.
    keyboardExpectedAt = null
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

/** What the fling is told, before anything is done about it. */
private const val FLING_TAG = "ListFling"

/**
 * The platform default, with the sign of the velocity put back the way the drag reads it.
 *
 * A flick on Aurora scrolled a list the right way while the finger was down and threw it back the
 * other way on release; a slow drag, which ends with no velocity and so produces no fling at all,
 * was fine throughout. That places the fault in the velocity rather than in the drag, and the
 * measurement says the same. Three flicks in one direction on the dev phone:
 *
 * ```
 * fling v=-5851.73,   unconsumed=-5757.672
 * fling v=-1241.1434, unconsumed=-0.0
 * fling v=-900.90607, unconsumed=-0.0
 * ```
 *
 * The numbers are consistent and plausibly sized, so the tracker is not producing noise — it is
 * producing the opposite of what the drag consumed. The first line is the giveaway: nearly all of
 * that velocity went unspent, i.e. the fling ran straight into an edge the list had just been
 * dragged away from. On the later two the list was mid-way and the fling had room, which is
 * exactly when the snap-back was visible.
 *
 * So the velocity is negated on the way in and the remainder on the way out, leaving the caller's
 * sign convention intact. The log line stays: it is the only way to notice if a future fork build
 * starts agreeing with the drag, at which point this correction has to go.
 */
@Composable
actual fun listFlingBehavior(): FlingBehavior {
    val delegate = ScrollableDefaults.flingBehavior()
    return remember(delegate) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                val left = with(delegate) { performFling(-initialVelocity) }
                Napier.i(tag = FLING_TAG) { "fling v=$initialVelocity flipped, unconsumed=$left" }
                return -left
            }
        }
    }
}

/**
 * The fork gives a `Dialog` a scene of its own, so the frame put around the root does not reach it
 * — see [DialogSceneFrame].
 */
@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) {
    AuroraWindowFrame { content() }
}
