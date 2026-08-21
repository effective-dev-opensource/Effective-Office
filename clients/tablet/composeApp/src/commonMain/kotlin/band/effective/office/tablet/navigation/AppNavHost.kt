package band.effective.office.tablet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.ui.inactivity.LocalInactivityTracking
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditor
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorViewModel
import band.effective.office.tablet.feature.fastBooking.presentation.FastBooking
import band.effective.office.tablet.feature.fastBooking.presentation.FastBookingViewModel
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoom
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomViewModel
import band.effective.office.tablet.feature.main.presentation.main.MainNavEvent
import band.effective.office.tablet.feature.main.presentation.main.MainScreen
import band.effective.office.tablet.feature.main.presentation.main.MainViewModel
import band.effective.office.tablet.feature.settings.SettingsScreen
import band.effective.office.tablet.platform.ModalBackHandler
import band.effective.office.tablet.platform.modalKeyboardPadding
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Matches the dim the pre-swap overlays used. */
private const val MODAL_SCRIM_ALPHA = 0.9f

/**
 * The app's navigation graph. Settings and Main are destinations; the modals are state-driven
 * overlays rather than `dialog<>` destinations. See Navigation in
 * clients/tablet/composeApp/README.md.
 */
@Composable
fun AppNavHost(startRoomConfigured: Boolean) {
    val navController = rememberNavController()
    var activeModal by remember { mutableStateOf<ActiveModal?>(null) }
    val startDestination: Any = if (startRoomConfigured) MainRoute else SettingsRoute

    // The screen underneath goes back to the room the tablet was set up with, so a modal left up
    // would address one room over a schedule for another.
    val inactivityTracking = LocalInactivityTracking.current
    LaunchedEffect(inactivityTracking) {
        inactivityTracking.timeouts.collect { activeModal = null }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable<SettingsRoute> {
                SettingsScreen(
                    onNavigateToMain = {
                        navController.navigate(MainRoute) {
                            popUpTo<SettingsRoute> { inclusive = true }
                        }
                    },
                )
            }

            composable<MainRoute> {
                MainScreen(
                    onNavigate = { event ->
                        activeModal = when (event) {
                            is MainNavEvent.OpenFastBooking ->
                                ActiveModal.FastBooking(event.minDuration)

                            is MainNavEvent.OpenFreeRoom ->
                                ActiveModal.FreeRoom(event.event, event.roomName)

                            is MainNavEvent.OpenBookingEditor ->
                                ActiveModal.BookingEditor(event.event, event.room)
                        }
                    },
                )
            }
        }

        val modal = activeModal
        if (modal != null) {
            val close: () -> Unit = { activeModal = null }
            ModalHost(onDismiss = close) {
                when (modal) {
                    is ActiveModal.FreeRoom -> {
                        val viewModel = koinViewModel<FreeSelectRoomViewModel> {
                            parametersOf(modal.event, modal.roomName)
                        }
                        FreeSelectRoom(onClose = close, viewModel = viewModel)
                    }

                    is ActiveModal.BookingEditor -> {
                        val viewModel = koinViewModel<BookingEditorViewModel> {
                            parametersOf(modal.event, modal.room)
                        }
                        BookingEditor(viewModel = viewModel, onClose = close)
                    }

                    is ActiveModal.FastBooking -> {
                        // Each RoomInfo carries its whole event list, so the rooms are read from
                        // the Main ViewModel still on the back stack instead of being carried here.
                        val mainEntry = remember { navController.getBackStackEntry<MainRoute>() }
                        val mainViewModel =
                            koinViewModel<MainViewModel>(viewModelStoreOwner = mainEntry)
                        val mainSnapshot = remember { mainViewModel.state.value }
                        val viewModel = koinViewModel<FastBookingViewModel> {
                            parametersOf(
                                modal.minEventDuration,
                                mainSnapshot.roomList[mainSnapshot.indexSelectRoom],
                                mainSnapshot.roomList,
                            )
                        }
                        FastBooking(viewModel = viewModel, onClose = close)
                    }
                }
            }
        }
    }
}

private sealed interface ActiveModal {
    data class FreeRoom(val event: EventInfo, val roomName: String) : ActiveModal
    data class BookingEditor(val event: EventInfo, val room: String) : ActiveModal
    data class FastBooking(val minEventDuration: Int) : ActiveModal
}

/**
 * Dimmed backdrop with a centered card: tapping the dim dismisses, taps on the card are absorbed,
 * and the back gesture is taken by [ModalBackHandler]. Owns a modal-scoped [ViewModelStoreOwner]
 * cleared on dispose, so the next modal starts on a fresh ViewModel.
 */
@Composable
private fun ModalHost(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBackHandler(onBack = onDismiss)

    val storeOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(Unit) {
        onDispose { storeOwner.viewModelStore.clear() }
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = MODAL_SCRIM_ALPHA))
                .modalKeyboardPadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
            ) {
                content()
            }
        }
    }
}
