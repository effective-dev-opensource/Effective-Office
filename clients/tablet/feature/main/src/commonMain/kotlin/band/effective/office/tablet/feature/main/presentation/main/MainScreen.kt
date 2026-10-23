package band.effective.office.tablet.feature.main.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.LoadMainScreen
import band.effective.office.tablet.core.ui.common.ErrorMainScreen
import kotlin.time.ExperimentalTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalTime::class)
@Composable
fun MainScreen(
    onNavigate: (MainNavEvent) -> Unit,
    viewModel: MainViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val currentDate by viewModel.currentTime.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect(onNavigate)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isError -> ErrorMainScreen(resetRequest = { viewModel.sendIntent(Intent.RebootRequest) })

            state.isLoad -> LoadMainScreen()

            state.isData -> {
                MainScreenView(
                    slotComponent = viewModel.slotComponent,
                    isDisconnect = state.isDisconnect,
                    roomList = state.roomList,
                    indexSelectRoom = state.indexSelectRoom,
                    timeToNextEvent = state.timeToNextEvent,
                    onRoomButtonClick = { viewModel.sendIntent(Intent.OnSelectRoom(it)) },
                    onCancelEventRequest = { viewModel.sendIntent(Intent.OnOpenFreeRoomModal) },
                    onFastBooking = { viewModel.sendIntent(Intent.OnFastBooking(it)) },
                    onIncrementData = { viewModel.sendIntent(Intent.OnUpdateSelectDate(updateInDays = 1)) },
                    onDecrementData = { viewModel.sendIntent(Intent.OnUpdateSelectDate(updateInDays = -1)) },
                    selectedDate = state.selectedDate,
                    currentDate = currentDate,
                    onOpenDateTimePickerModalRequest = {}, // TODO
                )
            }
        }
    }
}
