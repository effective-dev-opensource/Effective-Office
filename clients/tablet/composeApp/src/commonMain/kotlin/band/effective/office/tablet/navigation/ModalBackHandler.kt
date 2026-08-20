package band.effective.office.tablet.navigation

import androidx.compose.runtime.Composable

/**
 * Routes the platform's back gesture to [onBack] while a modal overlay is on screen, since an
 * overlay is not on the nav back stack. Not `androidx.compose.ui.backhandler.BackHandler`: this
 * Compose version ships no `ui-backhandler` artifact, and a new dependency would also have to
 * clear the Aurora fork's maven.
 */
@Composable
expect fun ModalBackHandler(onBack: () -> Unit)
