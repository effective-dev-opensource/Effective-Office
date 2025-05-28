package band.effective.office.tablet.ui.mainScreen.mainScreen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import band.effective.office.tablet.ui.common.ErrorMainScreen
import band.effective.office.tablet.ui.fastEvent.FastEventComponent
import band.effective.office.tablet.ui.fastEvent.FastEventView
import band.effective.office.tablet.ui.freeSelectRoom.FreeSelectRoomComponent
import band.effective.office.tablet.ui.freeSelectRoom.FreeSelectRoomView
import band.effective.office.tablet.ui.mainScreen.mainScreen.store.MainStore
import band.effective.office.tablet.ui.mainScreen.mainScreen.uiComponents.LoadMainScreen
import band.effective.office.tablet.ui.updateEvent.UpdateEventComponent
import band.effective.office.tablet.ui.updateEvent.UpdateEventView
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(component: MainComponent) {
    val state by component.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch {
            component.label.collect {
                when (it) {
                    is MainStore.Label.ShowToast -> Toast.makeText(
                        /* context = */ context,
                        /* text = */ it.text,
                        /* duration = */ Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    LaunchedEffect(state.showToast) {
        state.showToast.let { text ->
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    when {
        state.isError -> {
            ErrorMainScreen { component.sendIntent(MainStore.Intent.RebootRequest) }
        }

        state.isLoad -> {
            LoadMainScreen()
        }

        state.isData -> {
            MainScreenView(
                slotComponent = component.slotComponent,
                isDisconnect = state.isDisconnect,
                roomList = state.roomList,
                indexSelectRoom = state.indexSelectRoom,
                timeToNextEvent = state.timeToNextEvent,
                onRoomButtonClick = { component.sendIntent(MainStore.Intent.OnSelectRoom(it)) },
                onCancelEventRequest = { component.sendIntent(MainStore.Intent.OnOpenFreeRoomModal) },
                onFastBooking = { component.sendIntent(MainStore.Intent.OnFastBooking(it)) },
                onUpdate = { component.sendIntent(MainStore.Intent.OnUpdate) },
                onOpenDateTimePickerModalRequest = {},
                onIncrementData = {
                    component.sendIntent(MainStore.Intent.OnUpdateSelectDate(updateInDays = 1))
                },
                onDecrementData = {
                    component.sendIntent(MainStore.Intent.OnUpdateSelectDate(updateInDays = -1))
                },
                selectDate = state.selectDate
            ) { component.sendIntent(MainStore.Intent.OnResetSelectDate) }
        }

        state.isSettings -> {
            component.onSettings()
        }
    }
    val activeWindowSlot by component.modalWindowSlot.subscribeAsState()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = if (activeWindowSlot.child != null) Color.Black.copy(alpha = 0.9f) else Color.Transparent)
    ) {
        when (val activeComponent = activeWindowSlot.child?.instance) {
            is FreeSelectRoomComponent -> FreeSelectRoomView(freeSelectRoomComponent = activeComponent)
            is UpdateEventComponent -> UpdateEventView(component = activeComponent)
            is FastEventComponent -> FastEventView(component = activeComponent)
        }
    }
}
