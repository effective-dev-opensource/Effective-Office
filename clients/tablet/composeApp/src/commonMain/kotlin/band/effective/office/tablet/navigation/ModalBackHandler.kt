package band.effective.office.tablet.navigation

import androidx.compose.runtime.Composable

/**
 * Sends the platform's back gesture to [onBack] while a modal overlay is on screen. The overlay is
 * not a nav destination, so an uncaught gesture reaches the back stack instead and closes the app
 * with the modal still on screen.
 */
@Composable
expect fun ModalBackHandler(onBack: () -> Unit)
