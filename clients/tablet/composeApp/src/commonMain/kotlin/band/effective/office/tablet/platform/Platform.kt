package band.effective.office.tablet.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect val isDebug: Boolean

/** Whether the version overlay also carries the window size, density and font scale. */
expect val showsDebugMetrics: Boolean

/**
 * Sends the platform's back gesture to [onBack] while a modal overlay is on screen. The overlay is
 * not a nav destination, so an uncaught gesture reaches the back stack instead and closes the app
 * with the modal still on screen.
 */
@Composable
expect fun ModalBackHandler(onBack: () -> Unit)

/**
 * Keeps a modal overlay clear of the on-screen keyboard. A no-op where the host already shortens
 * the scene for it — subtracting the keyboard twice collapses the card. See Navigation in
 * clients/tablet/composeApp/README.md.
 */
expect fun Modifier.modalKeyboardPadding(): Modifier
