package band.effective.office.tablet.feature.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.model.nextEvent
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.feature.main.components.uiComponent.BusyRoomInfoComponent
import band.effective.office.tablet.feature.main.components.uiComponent.FreeRoomInfoComponent
import kotlin.time.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toDateTimePeriod
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.days

@Composable
fun RoomInfoComponent(
    modifier: Modifier = Modifier,
    room: RoomInfo,
    onOpenFreeRoomModalRequest: () -> Unit,
    timeToNextEvent: Int,
    isError: Boolean,
) {
    val paddings = 30.dp
    Column(modifier = modifier) {
        when {
            room.isFree() -> {
                FreeRoomInfoComponent(
                    modifier = Modifier.padding(paddings),
                    name = room.name,
                    capacity = room.capacity,
                    isHaveTv = room.isHaveTv,
                    electricSocketCount = room.socketCount,
                    isError = isError,
                    nextEvent = room.nextEvent(),
                    timeToNextEvent = timeToNextEvent
                )
            }

            room.isBusy() -> {
                BusyRoomInfoComponent(
                    modifier = Modifier.padding(paddings),
                    name = room.name,
                    capacity = room.capacity,
                    isHaveTv = room.isHaveTv,
                    electricSocketCount = room.socketCount,
                    event = room.currentEvent ?: EventInfo.emptyEvent,
                    onButtonClick = { onOpenFreeRoomModalRequest() },
                    timeToFinish = timeToNextEvent,
                    isError = isError
                )
            }
        }
    }
}

private fun RoomInfo.isFree() = currentEvent == null
private fun RoomInfo.isBusy() = currentEvent != null
