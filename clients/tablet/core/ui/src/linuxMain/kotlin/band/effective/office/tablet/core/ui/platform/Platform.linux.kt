package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
 * not report — shows up as closed on the next tick and the modal comes back down.
 */
@Composable
actual fun softKeyboardOverlapPx(): Int {
    val overlap by produceState(0) {
        while (true) {
            value = if (Keyboard.isOpen()) Keyboard.height().toInt() else 0
            delay(KEYBOARD_POLL_MS)
        }
    }
    return overlap
}
