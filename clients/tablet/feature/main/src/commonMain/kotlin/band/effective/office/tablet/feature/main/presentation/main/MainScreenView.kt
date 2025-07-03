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
import kotlinx.datetime.LocalDateTime

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
    onOpenDateTimePickerModalRequest: () -> Unit,
    onIncrementData: () -> Unit,
    onDecrementData: () -> Unit,
    selectedDate: LocalDateTime,
    currentDate: LocalDateTime,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            RoomInfoLeftPanel(
                slotComponent = slotComponent,
                selectedDate = selectedDate,
                currentDate = currentDate,
                onIncrementData = onIncrementData,
                onDecrementData = onDecrementData,
                roomList = roomList,
                indexSelectRoom = indexSelectRoom,
                onCancelEventRequest = onCancelEventRequest,
                timeToNextEvent = timeToNextEvent,
                isDisconnect = isDisconnect,
                onOpenDateTimePickerModal = onOpenDateTimePickerModalRequest,
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