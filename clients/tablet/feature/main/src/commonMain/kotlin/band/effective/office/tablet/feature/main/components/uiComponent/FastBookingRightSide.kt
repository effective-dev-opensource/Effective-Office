package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.fastbooking_button
import band.effective.office.tablet.feature.main.fastbooking_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun FastBookingRightSide(
    onFastBooking: (Int) -> Unit,
    roomList: List<RoomInfo>,
    indexSelectRoom: Int,
    onRoomButtonClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        FastBookingButtons(onBooking = onFastBooking)

        RoomList(
            list = roomList,
            indexSelectRoom = indexSelectRoom,
            onClick = onRoomButtonClick
        )
    }
}

@Composable
fun FastBookingButtons(onBooking: (Int) -> Unit) {
    Column {
        Text(
            text = stringResource(Res.string.fastbooking_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(10.dp))

        Row {
            val buttonModifier = Modifier.fillMaxWidth().weight(1f)
            listOf(15, 30, 60).forEachIndexed { index, time ->
                if (index != 0) {
                    Spacer(Modifier.width(10.dp))
                }
                Button(
                    modifier = buttonModifier,
                    onClick = { onBooking(time) }
                ) {
                    Text(stringResource(Res.string.fastbooking_button, time))
                }
            }
        }
    }
}

@Composable
fun RoomList(list: List<RoomInfo>, indexSelectRoom: Int, onClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        list.forEachIndexed { index, roomInfo ->
            val interactionSource = remember { MutableInteractionSource() }
            RoomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(
                        color = if (index == indexSelectRoom) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(bounded = false)
                    ) { onClick(index) }
                    .padding(10.dp),
                room = roomInfo,
            )
            Spacer(Modifier.height(5.dp))
        }

    }
}

@Composable
fun RoomButton(modifier: Modifier, room: RoomInfo) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(10.dp)
                    .background(
                        color = if (room.currentEvent == null) LocalCustomColorsPalette.current.freeStatus else LocalCustomColorsPalette.current.busyStatus,
                        shape = CircleShape
                    )
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = room.name,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.h5
            )
        }
        RoomProperty(
            spaceBetweenProperty = 20.dp,
            capacity = room.capacity,
            isHaveTv = room.isHaveTv,
            electricSocketCount = room.socketCount
        )
    }
}