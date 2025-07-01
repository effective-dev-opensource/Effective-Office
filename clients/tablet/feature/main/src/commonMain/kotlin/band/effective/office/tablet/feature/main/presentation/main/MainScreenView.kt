package band.effective.office.tablet.feature.main.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.feature.main.components.uiComponent.FastBookingRightSide
import band.effective.office.tablet.feature.main.components.uiComponent.RoomInfoLeftPanel
import band.effective.office.tablet.feature.main.presentation.slot.SlotComponent
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun MainScreenView(
    slotComponent: SlotComponent,
    isDisconnect: Boolean,
    roomList: List<RoomInfo>,
    indexSelectRoom: Int,
    timeToNextEvent: Int,
    onRoomButtonClick: (Int) -> Unit,
    onCancelEventRequest: () -> Unit,
    onFastBooking: (Int) -> Unit,
    onUpdate: () -> Unit,
    onOpenDateTimePickerModalRequest: () -> Unit,
    onIncrementData: () -> Unit,
    onDecrementData: () -> Unit,
    selectedDate: Instant,
    onResetDate: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            RoomInfoLeftPanel(
                slotComponent = slotComponent,
                selectedDate = selectedDate,
                onIncrementData = onIncrementData,
                onDecrementData = onDecrementData,
                onResetDate = onResetDate,
                roomList = roomList,
                indexSelectRoom = indexSelectRoom,
                onCancelEventRequest = onCancelEventRequest,
                timeToNextEvent = timeToNextEvent,
                isDisconnect = isDisconnect,
            )

            FastBookingRightSide(
                onFastBooking = onFastBooking,
                roomList = roomList,
                indexSelectRoom = indexSelectRoom,
                onRoomButtonClick = onRoomButtonClick,
            )
        }
    }
}