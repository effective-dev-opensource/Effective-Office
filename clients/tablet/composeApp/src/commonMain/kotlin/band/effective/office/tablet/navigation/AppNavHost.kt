package band.effective.office.tablet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditor
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorViewModel
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePicker
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePickerComponent
import band.effective.office.tablet.feature.fastBooking.presentation.FastBooking
import band.effective.office.tablet.feature.fastBooking.presentation.FastBookingViewModel
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomView
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomViewModel
import band.effective.office.tablet.feature.main.presentation.main.MainNavEvent
import band.effective.office.tablet.feature.main.presentation.main.MainScreen
import band.effective.office.tablet.feature.settings.SettingsScreen
import kotlin.reflect.typeOf
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateless nav constants for the modal `dialog<>` destinations (hoisted so they aren't
 *  re-allocated on recomposition). */
private object ModalNav {
    /** NavType map for routes carrying a single [EventInfo] payload. */
    val eventTypeMap = mapOf(typeOf<EventInfo>() to serializableNavType<EventInfo>())

    /** NavType map for the fast-booking route (a [RoomInfo] and a list of them). */
    val fastBookingTypeMap = mapOf(
        typeOf<RoomInfo>() to serializableNavType<RoomInfo>(),
        typeOf<List<RoomInfo>>() to serializableNavType<List<RoomInfo>>(),
    )

    /** Modals fill the screen so [DialogBackgroundDim] can draw the full-screen dim behind the
     *  centered content. */
    val dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
}

/**
 * The app's navigation graph. Two full-screen destinations (Settings/Main) and the modals — plus the
 * date/time picker — as `dialog<>` destinations. Each modal is its own dialog window (not a
 * state-driven overlay); the date/time picker is pushed on top of the booking editor and shares its
 * ViewModel.
 *
 * @param startRoomConfigured whether a room is already configured (drives the start destination)
 */
@Composable
fun AppNavHost(startRoomConfigured: Boolean) {
    val navController = rememberNavController()
    val startDestination: Any = if (startRoomConfigured) MainRoute else SettingsRoute

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
                    when (event) {
                        is MainNavEvent.OpenFastBooking -> navController.navigate(
                            FastBookingRoute(event.minDuration, event.selectedRoom, event.rooms)
                        )

                        is MainNavEvent.OpenFreeRoom -> navController.navigate(
                            FreeRoomRoute(event.event, event.roomName)
                        )

                        is MainNavEvent.OpenBookingEditor -> navController.navigate(
                            BookingFlowRoute(event.event, event.room)
                        )
                    }
                },
            )
        }

        dialog<FreeRoomRoute>(typeMap = ModalNav.eventTypeMap, dialogProperties = ModalNav.dialogProperties) { entry ->
            val route = entry.toRoute<FreeRoomRoute>()
            val viewModel = koinViewModel<FreeSelectRoomViewModel> {
                parametersOf(route.event, route.roomName)
            }
            DialogBackgroundDim(onDismiss = { navController.popBackStack() }) {
                FreeSelectRoomView(
                    onClose = { navController.popBackStack() },
                    viewModel = viewModel,
                )
            }
        }

        navigation<BookingFlowRoute>(
            startDestination = BookingEditorRoute,
            typeMap = ModalNav.eventTypeMap,
        ) {
            dialog<BookingEditorRoute>(dialogProperties = ModalNav.dialogProperties) {
                val flowEntry = remember(it) { navController.getBackStackEntry<BookingFlowRoute>() }
                val route = flowEntry.toRoute<BookingFlowRoute>()
                val viewModel = koinViewModel<BookingEditorViewModel>(viewModelStoreOwner = flowEntry) {
                    parametersOf(route.event, route.room)
                }
                DialogBackgroundDim(onDismiss = { navController.popBackStack() }) {
                    BookingEditor(
                        viewModel = viewModel,
                        onClose = { navController.popBackStack() },
                        onOpenDateTimePicker = { navController.navigate(DateTimePickerRoute) },
                    )
                }
            }

            dialog<DateTimePickerRoute>(dialogProperties = ModalNav.dialogProperties) {
                val flowEntry = remember(it) { navController.getBackStackEntry<BookingFlowRoute>() }
                val viewModel = koinViewModel<BookingEditorViewModel>(viewModelStoreOwner = flowEntry)
                val component = viewModel.dateTimePickerComponent
                val pickerState by component.state.collectAsState()
                val close: () -> Unit = {
                    component.sendIntent(DateTimePickerComponent.Intent.CloseModal)
                    navController.popBackStack()
                }
                DialogBackgroundDim(onDismiss = close) {
                    DateTimePicker(
                        currentDate = pickerState.currentDate,
                        onCloseRequest = close,
                        onChangeDate = { component.sendIntent(DateTimePickerComponent.Intent.OnChangeDate(it)) },
                        onChangeTime = { component.sendIntent(DateTimePickerComponent.Intent.OnChangeTime(it)) },
                        enableDateButton = pickerState.isEnabledButton,
                    )
                }
            }
        }

        dialog<FastBookingRoute>(typeMap = ModalNav.fastBookingTypeMap, dialogProperties = ModalNav.dialogProperties) { entry ->
            val route = entry.toRoute<FastBookingRoute>()
            val viewModel = koinViewModel<FastBookingViewModel> {
                parametersOf(route.minEventDuration, route.selectedRoom, route.rooms)
            }
            DialogBackgroundDim(onDismiss = { navController.popBackStack() }) {
                FastBooking(
                    viewModel = viewModel,
                    onClose = { navController.popBackStack() },
                )
            }
        }
    }
}
