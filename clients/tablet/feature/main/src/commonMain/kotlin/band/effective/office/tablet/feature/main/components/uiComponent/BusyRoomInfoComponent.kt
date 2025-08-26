package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.core.ui.theme.roomInfoColor
import band.effective.office.tablet.core.ui.theme.undefineStateColor
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.room_occupancy_date
import band.effective.office.tablet.feature.main.room_occupancy_time
import band.effective.office.tablet.feature.main.stop_meeting_button
import org.jetbrains.compose.resources.stringResource

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
                text = stringResource(Res.string.room_occupancy_date, DateDisplayMapper.formatTime(event.finishTime)) +
                        " " + if (timeToFinish > 0) stringResource(Res.string.room_occupancy_time, timeToFinish.getDuration()) else "",
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