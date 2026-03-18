package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.core.ui.theme.roomInfoColor
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.data_not_upd
import band.effective.office.tablet.feature.main.free_now
import band.effective.office.tablet.feature.main.free_room_occupancy_date
import band.effective.office.tablet.feature.main.free_room_occupancy_time
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun untilText(
    startTime: LocalDateTime,
    timeToFinish: Int
) = buildString {
    val until =
        stringResource(Res.string.free_room_occupancy_date, DateDisplayMapper.formatTime(startTime))
    append(until)
    if (timeToFinish > 0) {
        val toFinish =
            stringResource(Res.string.free_room_occupancy_time, timeToFinish.getDuration())
        append(" $toFinish")
    }
}

@Composable
private fun untilOrErrorText(
    startTime: LocalDateTime?,
    timeToFinish: Int,
    isError: Boolean
) = when {
    isError ->  stringResource(Res.string.data_not_upd)
    startTime == null -> stringResource(Res.string.free_now)
    else -> untilText(startTime, timeToFinish)
}

@Composable
fun FreeRoomInfoComponent(
    modifier: Modifier = Modifier,
    name: String,
    capacity: Int,
    isHaveTv: Boolean,
    electricSocketCount: Int,
    isError: Boolean,
    nextEvent: EventInfo?,
    timeToNextEvent: Int
) {
    CommonRoomInfoComponent(
        modifier = modifier,
        name = name,
        capacity = capacity,
        isHaveTv = isHaveTv,
        electricSocketCount = electricSocketCount,
        backgroundColor = LocalCustomColorsPalette.current.freeStatus,
        isError = isError
    ) {
        Text(
            text = untilOrErrorText(
                startTime = nextEvent?.startTime,
                timeToFinish = timeToNextEvent,
                isError = isError
            ),
            style = MaterialTheme.typography.h5,
            color = roomInfoColor,
        )
    }
}

// untilOrErrorText: нормальный случай — есть следующее событие и осталось время
@Preview(widthDp = 700, heightDp = 300, locale = "ru")
@Composable
private fun PreviewFreeRoomInfoComponent_WithNextEvent() {
    AppTheme {
        FreeRoomInfoComponent(
            modifier = Modifier.padding(30.dp),
            name = "Sun",
            capacity = 8,
            isHaveTv = true,
            electricSocketCount = 3,
            isError = false,
            nextEvent = EventInfo(
                startTime = LocalDateTime(2024, 3, 15, 10, 0),
                finishTime = LocalDateTime(2024, 3, 15, 11, 30),
                organizer = Organizer(fullName = "John Doe", id = "1", email = "john@example.com"),
                id = "event-1",
                isLoading = false,
                isEditable = true
            ),
            timeToNextEvent = 1
        )
    }
}

// untilOrErrorText: нормальный случай — есть следующее событие, timeToFinish = 0 (время не отображается)
@Preview(widthDp = 700, heightDp = 300, locale = "ru")
@Composable
private fun PreviewFreeRoomInfoComponent_WithNextEventNoTimeToFinish() {
    AppTheme {
        FreeRoomInfoComponent(
            modifier = Modifier.padding(30.dp),
            name = "Sun",
            capacity = 8,
            isHaveTv = true,
            electricSocketCount = 3,
            isError = false,
            nextEvent = EventInfo(
                startTime = LocalDateTime(2024, 3, 15, 10, 0),
                finishTime = LocalDateTime(2024, 3, 15, 11, 30),
                organizer = Organizer(fullName = "John Doe", id = "1", email = "john@example.com"),
                id = "event-1",
                isLoading = false,
                isEditable = true
            ),
            timeToNextEvent = 0
        )
    }
}

// untilOrErrorText: startTime == null — нет следующего мероприятия
@Preview(widthDp = 700, heightDp = 300, locale = "ru")
@Composable
private fun PreviewFreeRoomInfoComponent_NoNextEvent() {
    AppTheme {
        FreeRoomInfoComponent(
            modifier = Modifier.padding(30.dp),
            name = "Sun",
            capacity = 8,
            isHaveTv = true,
            electricSocketCount = 3,
            isError = false,
            nextEvent = null,
            timeToNextEvent = 0
        )
    }
}

// untilOrErrorText: isError == true — свободна сейчас (ошибка загрузки)
@Preview(widthDp = 700, heightDp = 300, locale = "ru")
@Composable
private fun PreviewFreeRoomInfoComponent_Error() {
    AppTheme {
        FreeRoomInfoComponent(
            modifier = Modifier.padding(30.dp),
            name = "Sun",
            capacity = 8,
            isHaveTv = true,
            electricSocketCount = 3,
            isError = true,
            nextEvent = null,
            timeToNextEvent = 0
        )
    }
}