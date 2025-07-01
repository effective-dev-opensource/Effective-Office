package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.ui.common.Disconnect
import band.effective.office.tablet.core.ui.date.DateTimeView
import band.effective.office.tablet.feature.main.components.RoomInfoComponent
import band.effective.office.tablet.feature.main.presentation.slot.State
import band.effective.office.tablet.feature.main.presentation.slot.components.SlotView
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun RoomInfoLeftPanel(
    slotState: State,
    selectedDate: Instant,
    onIncrementData: () -> Unit,
    onDecrementData: () -> Unit,
    onResetDate: () -> Unit,
    roomList: List<RoomInfo>,
    indexSelectRoom: Int,
    onCancelEventRequest: () -> Unit,
    timeToNextEvent: Int,
    isDisconnect: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.6f)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        ) {
            item {
                DateTimeView(
                    modifier = Modifier.padding(
                        start = 30.dp,
                        top = 50.dp,
                        end = 20.dp,
                        bottom = 0.dp
                    ).height(70.dp),
                    selectDate = selectedDate,
                    increment = onIncrementData,
                    decrement = onDecrementData,
                    onOpenDateTimePickerModal = { },
                    currentDate = Clock.System.now(),
                    back = onResetDate,
                )
            }

            stickyHeader {
                RoomInfoComponent(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background),
                    room = roomList[indexSelectRoom],
                    onOpenFreeRoomModalRequest = { onCancelEventRequest() },
                    timeToNextEvent = timeToNextEvent,
                    isError = isDisconnect,
                )
            }

            items(slotState.slots) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {
                    SlotView(
                        slotUi = it,
                        onClick = {
                            /*slotComponent.sendIntent( // TODO
                                SlotStore.Intent.ClickOnSlot(
                                    this
                                )
                            )*/
                        },
                        onCancel = {
                            /*slotComponent.sendIntent( // TODO
                                SlotStore.Intent.OnCancelDelete(
                                    it
                                )
                            )*/
                        }
                    )
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        if (isDisconnect) {
            Disconnect(modifier = Modifier.padding(horizontal = 30.dp), visible = isDisconnect)
        }
    }
}

