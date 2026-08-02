package band.effective.office.tablet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditor
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorViewModel
import band.effective.office.tablet.feature.fastBooking.presentation.FastBooking
import band.effective.office.tablet.feature.fastBooking.presentation.FastBookingViewModel
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomView
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomViewModel
import band.effective.office.tablet.feature.main.presentation.main.MainNavEvent
import band.effective.office.tablet.feature.main.presentation.main.MainScreen
import band.effective.office.tablet.feature.main.presentation.main.MainViewModel
import band.effective.office.tablet.feature.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * The app's navigation graph.
 *
 * Settings/Main are `NavHost` destinations. Modal windows are **state-driven overlays** rendered in
 * the main composition, NOT Compose `Dialog` windows (`dialog<>` destinations): calf's date/time
 * pickers are native UIKit views on iOS, and inside a Compose dialog window they receive no touches
 * at all — the calendar does not select and the wheels do not scroll (Calf issue #115, "native
 * picker draws on the wrong view inside a container"). So the modals live in the main scene, the way
 * the pre-swap Decompose overlays did, and only the picker itself gets a Compose `Dialog` — see
 * [band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePicker],
 * whose present animation masks the frame where calf has not applied our colors yet.
 *
 * @param startRoomConfigured whether a room is already configured (drives the start destination)
 */
@Composable
fun AppNavHost(startRoomConfigured: Boolean) {
    val navController = rememberNavController()
    var activeModal by remember { mutableStateOf<ActiveModal?>(null) }
    val startDestination: Any = if (startRoomConfigured) MainRoute else SettingsRoute

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
                        FreeSelectRoomView(onClose = close, viewModel = viewModel)
                    }

                    is ActiveModal.BookingEditor -> {
                        val viewModel = koinViewModel<BookingEditorViewModel> {
                            parametersOf(modal.event, modal.room)
                        }
                        BookingEditor(viewModel = viewModel, onClose = close)
                    }

                    is ActiveModal.FastBooking -> {
                        // The room list and the selected room are read from the Main ViewModel
                        // rather than carried in the modal: each RoomInfo carries its full event
                        // list, and Main is still on the back stack behind the overlay.
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

/** In-memory description of the currently-open modal (replaces the serializable modal routes). */
private sealed interface ActiveModal {
    data class FreeRoom(val event: EventInfo, val roomName: String) : ActiveModal
    data class BookingEditor(val event: EventInfo, val room: String) : ActiveModal
    data class FastBooking(val minEventDuration: Int) : ActiveModal
}

/**
 * Full-screen dim (0.9 black, matching the pre-swap Decompose overlay) behind a centered modal,
 * rendered in-composition. Provides a modal-scoped [ViewModelStoreOwner] that is cleared when the
 * modal leaves the composition, so each modal gets a fresh ViewModel. Tapping the dim dismisses;
 * taps on the content are absorbed.
 *
 * No rotation/density/inactivity wrappers here, unlike the `dialog<>` hosting this replaced: an
 * overlay is part of the main scene, so the ones `AppRoot` installs already cover it.
 */
@Composable
private fun ModalHost(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
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
                .background(Color.Black.copy(alpha = 0.9f))
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
