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
import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlin.time.ExperimentalTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalTime::class)
@Composable
fun MainScreen(
    onNavigate: (MainNavEvent) -> Unit,
    viewModel: MainViewModel = koinViewModel(),
) {
    val component = viewModel
    val state by component.state.collectAsState()
    val currentDate by CurrentTimeHolder.currentTime.collectAsState()

    LaunchedEffect(Unit) {
        component.navEvents.collect(onNavigate)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isError -> ErrorMainScreen(resetRequest = { component.sendIntent(Intent.RebootRequest) })

            state.isLoad -> LoadMainScreen()

            state.isData -> {
                MainScreenView(
                    slotComponent = component.slotComponent,
                    isDisconnect = state.isDisconnect,
                    roomList = state.roomList,
                    indexSelectRoom = state.indexSelectRoom,
                    timeToNextEvent = state.timeToNextEvent,
                    onRoomButtonClick = { component.sendIntent(Intent.OnSelectRoom(it)) },
                    onCancelEventRequest = { component.sendIntent(Intent.OnOpenFreeRoomModal) },
                    onFastBooking = { component.sendIntent(Intent.OnFastBooking(it)) },
                    onIncrementData = { component.sendIntent(Intent.OnUpdateSelectDate(updateInDays = 1)) },
                    onDecrementData = { component.sendIntent(Intent.OnUpdateSelectDate(updateInDays = -1)) },
                    selectedDate = state.selectedDate,
                    currentDate = currentDate,
                    onOpenDateTimePickerModalRequest = {}, // TODO
                )
            }
        }
    }
}
