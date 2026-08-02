package band.effective.office.tablet.navigation

import androidx.compose.runtime.Composable

/**
 * Routes the platform's back gesture to [onBack] while the modal overlay is on screen.
 *
 * The modals are not on the nav back stack any more (see [AppNavHost]), so on Android back would
 * otherwise fall through to the `NavHost`, find nothing above the start destination and close the
 * app with the modal still drawn.
 *
 * Not `androidx.compose.ui.backhandler.BackHandler`: that lives in a separate `ui-backhandler`
 * artifact this Compose version does not ship, and pulling a new dependency in would also have to
 * clear the Aurora fork's own maven. Android is the only platform here with a back gesture at all,
 * so an expect/actual costs less — iOS and Aurora get a no-op.
 */
@Composable
expect fun ModalBackHandler(onBack: () -> Unit)
