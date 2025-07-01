package band.effective.office.tablet.feature.main.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.LoadMainScreen
import band.effective.office.tablet.core.ui.common.ErrorMainScreen
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun MainScreen(component: MainComponent) {
    val state by component.state.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isError -> ErrorMainScreen(resetRequest = { component.sendIntent(Intent.RebootRequest) })

            state.isLoad -> LoadMainScreen()

            state.isData -> {
                MainScreenView(
                    slotComponent = component.slotComponent,
                    isDisconnect = state.isDisconnect,
                    roomList = state.roomList,
                    indexSelectRoom = state.indexSelectRoom,
                    timeToNextEvent = state.timeToNextEvent,
                    onRoomButtonClick = { component.sendIntent(Intent.OnSelectRoom(it)) },
                    onCancelEventRequest = { component.sendIntent(Intent.OnOpenFreeRoomModal) },
                    onFastBooking = { component.sendIntent(Intent.OnFastBooking(it)) },
                    onUpdate = { component.sendIntent(Intent.OnUpdate) },
                    onIncrementData = { component.sendIntent(Intent.OnUpdateSelectDate(updateInDays = 1)) },
                    onDecrementData = { component.sendIntent(Intent.OnUpdateSelectDate(updateInDays = -1)) },
                    selectedDate = state.selectedDate,
                    onResetDate = { component.sendIntent(Intent.OnResetSelectDate) },
                    onOpenDateTimePickerModalRequest = {}, // TODO
                )
            }

            state.isSettings -> {
                component.onSettings()
            }
        }
    }

    /*val activeWindowSlot by component.modalWindowSlot.subscribeAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = if (activeWindowSlot.child != null)
                    Color.Black.copy(alpha = 0.9f)
                else Color.Transparent
            )
    ) {
        when (val activeComponent = activeWindowSlot.child?.instance) {
            /*is FreeSelectRoomComponent -> FreeSelectRoomView(freeSelectRoomComponent = activeComponent)
            is UpdateEventComponent -> UpdateEventView(component = activeComponent)
            is FastEventComponent -> FastEventView(component = activeComponent)*/
        }
    }*/
}
