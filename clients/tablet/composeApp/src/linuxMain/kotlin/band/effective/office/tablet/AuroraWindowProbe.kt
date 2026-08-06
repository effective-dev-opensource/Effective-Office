package band.effective.office.tablet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalWindowInfo
import io.github.aakira.napier.Napier
import ru.auroraos.kmp.window.Window
import ru.auroraos.kmp.window.WindowEvents

private const val WINDOW_TAG = "AuroraWindow"

/**
 * Diagnostics for one question: does the window change size when the keyboard opens?
 *
 * If it does, the difference is the keyboard's inset — measured, in the window's own coordinates,
 * with no guessing about which way the keyboard is turned. That would replace the only number we
 * have today, `Keyboard.height()`, which on the tablet answers the screen's whole long side (2000
 * of a 1200x2000 window) because maliit's surface spans the screen and the key strip's thickness
 * is nowhere in the binding.
 *
 * If it does not — and the fork reporting no insets at all suggests it will not — the log stays
 * silent through a keyboard opening, and that silence is the answer.
 *
 * Nothing here changes behaviour; it only writes to the journal, under its own tag:
 * `journalctl --no-pager | grep -E 'AuroraWindow|SoftKeyboard|OrganizerPicker|ModalHost'`
 */
fun installWindowProbe() {
    WindowEvents.listenWindowSize { _, event ->
        Napier.i(tag = WINDOW_TAG) { "window resized to ${event.width}x${event.height}" }
    }
}

/**
 * The other half: what the window and the scene think their sizes are, once there is a frame to
 * ask after. Read side by side, the screen size and Compose's container size say whether the scene
 * covers the whole window and which side is the long one — both needed to read the resize events,
 * and both unverified on the tablet so far.
 */
@Composable
fun AuroraWindowProbe() {
    val windowInfo = LocalWindowInfo.current
    LaunchedEffect(Unit) {
        withFrameNanos { }
        val container = windowInfo.containerSize
        Napier.i(tag = WINDOW_TAG) {
            "screen ${Window.screenWidth()}x${Window.screenHeight()}, " +
                "scene ${container.width}x${container.height}, " +
                "contentScale ${Window.contentScale()}"
        }
    }
}
