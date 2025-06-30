package band.effective.office.tablet.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.date.DatePickerView
import band.effective.office.tablet.core.ui.date.DateTimeView
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalTime::class)
@Composable
fun MainScreenView(
    isDisconnect: Boolean,
    roomList: List<Any>,
    indexSelectRoom: Int,
    timeToNextEvent: Int,
    onRoomButtonClick: (Int) -> Unit,
    onCancelEventRequest: () -> Unit,
    onFastBooking: (Int) -> Unit,
    onUpdate: () -> Unit,
    onIncrementData: () -> Unit,
    onDecrementData: () -> Unit,
    selectedDate: Instant,
    onResetDate: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left side - Room info and calendar
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
                            onOpenDateTimePickerModal = {  },
                            currentDate = Clock.System.now(),
                            back = onResetDate,
                        )
                    }

                    item {
                        // Room info
                        RoomInfo(
                            roomName = if (roomList.isNotEmpty() && indexSelectRoom < roomList.size)
                                "Room ${indexSelectRoom + 1}" else "No room selected",
                            timeToNextEvent = timeToNextEvent,
                            onCancelEvent = onCancelEventRequest,
                            isDisconnect = isDisconnect
                        )
                    }

                    // Here would be the slots, but we don't have SlotComponent in the new implementation
                    item {
                        Text(
                            "Events would be displayed here",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Disconnect indicator
                if (isDisconnect) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Disconnected",
                            color = Color.White
                        )
                    }
                }
            }

            // Right side - Fast booking buttons and room list
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Fast booking buttons
                FastBookingButtons(onBooking = onFastBooking)

                // Room list
                RoomList(
                    count = roomList.size,
                    indexSelectRoom = indexSelectRoom,
                    onClick = onRoomButtonClick
                )
            }
        }
    }
}

@Composable
fun RoomInfo(
    roomName: String,
    timeToNextEvent: Int,
    onCancelEvent: () -> Unit,
    isDisconnect: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            roomName,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Time to next event: $timeToNextEvent minutes",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (timeToNextEvent > 0) {
            Button(onClick = onCancelEvent) {
                Text("Cancel Event")
            }
        }
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
fun RoomList(count: Int, indexSelectRoom: Int, onClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in 0 until count) {
            RoomButton(
                modifier = Modifier
                    .background(
                        color = if (i == indexSelectRoom) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onClick(i) }
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .padding(10.dp),
                roomName = "Room ${i + 1}",
                isOccupied = false
            )
            Spacer(Modifier.height(5.dp))
        }
    }
}

@Composable
fun RoomButton(modifier: Modifier, roomName: String, isOccupied: Boolean) {
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
                        color = if (isOccupied) Color.Red else Color.Green,
                        shape = CircleShape
                    )
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = roomName,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}