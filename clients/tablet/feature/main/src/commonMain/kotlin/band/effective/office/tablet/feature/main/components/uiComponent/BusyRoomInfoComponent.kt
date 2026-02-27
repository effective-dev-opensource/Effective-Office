package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.core.ui.theme.roomInfoColor
import band.effective.office.tablet.core.ui.theme.undefineStateColor
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.room_occupancy_date
import band.effective.office.tablet.feature.main.room_occupancy_time
import band.effective.office.tablet.feature.main.stop_meeting_button
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun untilText(
    finishTime: LocalDateTime,
    timeToFinish: Int
) = buildString {
    val until = stringResource(Res.string.room_occupancy_date, DateDisplayMapper.formatTime(finishTime))
    append(until)
    if (timeToFinish > 0){
        val toFinish = stringResource(Res.string.room_occupancy_time, timeToFinish.getDuration())
        append(" $toFinish")
    }
}

@Composable
fun BusyRoomInfoComponent(
    modifier: Modifier = Modifier,
    name: String,
    capacity: Int,
    isHaveTv: Boolean,
    electricSocketCount: Int,
    event: EventInfo,
    onButtonClick: () -> Unit,
    timeToFinish: Int,
    isError: Boolean
) {
    val backgroundColor = LocalCustomColorsPalette.current.busyStatus

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val correctBackgroundColor = if (isError) undefineStateColor else backgroundColor
    val colorButton = if (isPressed) roomInfoColor else correctBackgroundColor
    val colorTextButton = if (isPressed) correctBackgroundColor else roomInfoColor

    Column {
        CommonRoomInfoComponent(
            modifier = modifier,
            name = name,
            capacity = capacity,
            isHaveTv = isHaveTv,
            electricSocketCount = electricSocketCount,
            backgroundColor = backgroundColor,
            isError = isError
        ) {
            Text(
                text = untilText(event.finishTime, timeToFinish),
                style = MaterialTheme.typography.h5,
                color = roomInfoColor
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = event.organizer.fullName,
                style = MaterialTheme.typography.h5,
                color = roomInfoColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (event.isEditable) {
                Button(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(40.dp))
                        .height(45.dp)
                        .background(color = backgroundColor)
                        .border(
                            width = 3.dp,
                            color = roomInfoColor,
                            shape = RoundedCornerShape(40.dp),
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorButton
                    ),
                    interactionSource = interactionSource,
                    onClick = onButtonClick
                ) {
                    Text(text = stringResource(Res.string.stop_meeting_button), color = colorTextButton)
                }
            }
        }
    }
}

@Preview(widthDp = 700, heightDp = 300, locale = "ru")
@Composable
private fun PreviewBusyRoomInfoComponent() {
    AppTheme {
        BusyRoomInfoComponent(
            modifier = Modifier.padding(30.dp),
            name = "Sun",
            capacity = 8,
            isHaveTv = true,
            electricSocketCount = 3,
            event = EventInfo(
                startTime = LocalDateTime(2024, 3, 15, 10, 0),
                finishTime = LocalDateTime(2024, 3, 15, 11, 30),
                organizer = Organizer(fullName = "John Doe", id = "1", email = "john@example.com"),
                id = "event-1",
                isLoading = false,
                isEditable = true
            ),
            onButtonClick = {},
            timeToFinish = 45,
            isError = false
        )
    }
}
