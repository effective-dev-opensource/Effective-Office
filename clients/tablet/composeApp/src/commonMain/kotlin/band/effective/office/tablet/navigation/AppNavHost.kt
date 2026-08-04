package band.effective.office.tablet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeightIn
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.ui.inactivity.InactivityTracking
import band.effective.office.tablet.core.ui.platform.LocalFocusedFieldBottom
import band.effective.office.tablet.core.ui.platform.softKeyboardOverlapPx
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
import kotlin.math.roundToInt

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

    // The tablet returns to the room it was set up with when nobody has touched it for a minute.
    // The modal has to go with it: it addresses the room it was opened for, so leaving it up would
    // put "Book B" over a screen that has already gone back to A — and the next person books the
    // wrong room. Closing the overlay takes the date/time picker with it, since that lives inside
    // the booking editor's composition.
    LaunchedEffect(Unit) {
        InactivityTracking.timeouts.collect { activeModal = null }
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

/** How much air to leave between the field being typed into and the top of the keyboard. */
private val FIELD_TO_KEYBOARD_GAP = 8.dp

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
 * overlay is part of the main scene, so the ones `AppRoot` installs already cover it. The back
 * gesture, on the other hand, has to be caught here — see [ModalBackHandler].
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
        // Move the card out from under the keyboard, rather than padding the box it sits in:
        // padding shrinks that box, and a card taller than what is left gets squeezed and clipped
        // from the inside instead of by the screen edge. How far to move is per-platform — see
        // [softKeyboardShiftPx].
        //
        // Keeping the card at full height is the other half of the same problem. On iOS the system
        // shortens the scene when the keyboard opens, and the card would be measured against the
        // shorter box and shrink — the squeezing. It cannot simply be measured unbounded either:
        // the card scrolls inside, and Compose refuses an infinite height above a scrolling
        // container. So it is measured against the tallest the box has ever been, which is the
        // screen before the keyboard took its share.
        var fullHeight by remember { mutableStateOf(0) }
        val density = LocalDensity.current

        // What has to clear the keyboard is the focused field, not the card around it: aim for its
        // bottom edge sitting [FIELD_TO_KEYBOARD_GAP] above the keyboard, and move the card by
        // however much that costs. Nothing focused, or the field already high enough — no shift.
        val focusedFieldBottom = remember { mutableStateOf<Int?>(null) }
        val overlapPx = softKeyboardOverlapPx()
        val gapPx = with(density) { FIELD_TO_KEYBOARD_GAP.roundToPx() }
        // Measured rather than taken from the window size: on iOS `LocalWindowInfo.containerSize`
        // comes back with the sides swapped — 1668 as the height of a 1668x2420 portrait screen —
        // and everything derived from it lands nowhere near the keyboard.
        var containerBottom by remember { mutableStateOf(0) }
        var containerHeight by remember { mutableStateOf(0) }
        var cardHeight by remember { mutableStateOf(0) }

        // Where the field sits with nothing shifted, captured once while the shift is still zero.
        // Deriving it from the live position instead would feed the shift back into its own input:
        // move the card, the field moves with it, the number shrinks, the shift shrinks — and the
        // card jitters between two positions. The keyboard is still animating at this point, so the
        // shift keeps recomputing against a moving keyboardTop, which is fine — that end is real.
        var restingFieldBottom by remember { mutableStateOf<Int?>(null) }
        val measuredFieldBottom = focusedFieldBottom.value
        if (measuredFieldBottom == null) {
            restingFieldBottom = null
        } else if (restingFieldBottom == null) {
            restingFieldBottom = measuredFieldBottom
        }
        // Positive lifts the card, negative lets it back down. Down matters on iOS: the system
        // shortens the scene there, the card is centred in what is left and ends up further above
        // the keyboard than asked for. It may only come down by as much as it hangs off the top,
        // so a card that already fits never drifts below where it was centred.
        val overhangTop = maxOf(0, (cardHeight - containerHeight) / 2)
        val shiftPx = restingFieldBottom
            ?.let { (it + gapPx - (containerBottom - overlapPx)).coerceAtLeast(-overhangTop) }
            ?: 0

        // iOS shortens the scene when the keyboard opens, which re-lays out the card underneath us,
        // so a resting position taken before that describes a layout that no longer exists. Retake
        // it whenever the scene resizes: measured + shift is where the field would be with nothing
        // shifted, so this stays a fixed point rather than chasing itself.
        LaunchedEffect(containerBottom) {
            if (measuredFieldBottom != null) restingFieldBottom = measuredFieldBottom + shiftPx
        }

        // A tap on the dim takes one step back, not two: with the keyboard up it puts the keyboard
        // away and leaves the modal, and only closes the modal once there is no keyboard to close.
        // iOS has no dismiss key on its keyboard, so without this the only way out of the keyboard
        // is to close the whole dialog.
        val focusManager = LocalFocusManager.current
        val onDimTap: () -> Unit = {
            if (focusedFieldBottom.value != null) focusManager.clearFocus() else onDismiss()
        }

        CompositionLocalProvider(LocalFocusedFieldBottom provides focusedFieldBottom) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        containerHeight = it.height
                        fullHeight = maxOf(fullHeight, it.height)
                    }
                    .onGloballyPositioned {
                        containerBottom = (it.positionInWindow().y + it.size.height).roundToInt()
                    }
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDimTap,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .then(
                            if (fullHeight > 0) {
                                Modifier.requiredHeightIn(max = with(density) { fullHeight.toDp() })
                            } else {
                                Modifier
                            }
                        )
                        // A draw-time translation, not a layout offset: the field reports its
                        // position from layout, and moving it there would feed our own shift back
                        // into the number we derive the shift from.
                        .graphicsLayer { translationY = -shiftPx.toFloat() }
                        .onSizeChanged { cardHeight = it.height }
                        .clickable(
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
}
