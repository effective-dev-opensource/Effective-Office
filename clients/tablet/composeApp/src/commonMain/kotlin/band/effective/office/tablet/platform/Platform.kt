package band.effective.office.tablet.platform

import androidx.compose.runtime.Composable

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
