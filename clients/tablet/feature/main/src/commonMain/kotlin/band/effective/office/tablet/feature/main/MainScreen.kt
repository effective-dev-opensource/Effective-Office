package band.effective.office.tablet.feature.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun MainScreen(component: MainComponent) {
    val state by component.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isError -> {
                Text("Error occurred")
                Button(onClick = { component.sendIntent(Intent.RebootRequest) }) {
                    Text("Retry")
                }
            }

            state.isLoad -> {
                Text("Loading...")
            }

            state.isData -> {
                MainScreenView(
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
                    onResetDate = { component.sendIntent(Intent.OnResetSelectDate) }
                )
            }

            state.isSettings -> {
                component.onSettings()
            }
        }
    }
}
