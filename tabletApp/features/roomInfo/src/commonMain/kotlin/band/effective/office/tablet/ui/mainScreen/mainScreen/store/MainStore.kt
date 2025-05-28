package band.effective.office.tablet.ui.mainScreen.mainScreen.store

import band.effective.office.tablet.domain.model.EventInfo
import band.effective.office.tablet.domain.model.RoomInfo
import com.arkivanov.mvikotlin.core.store.Store
import java.util.Calendar
import java.util.GregorianCalendar

interface MainStore : Store<MainStore.Intent, MainStore.State, MainStore.Label> {
    sealed interface Intent {
        object OnOpenFreeRoomModal : Intent
        object RebootRequest : Intent
        data class OnChangeEventRequest(val eventInfo: EventInfo) : Intent
        data class OnSelectRoom(val index: Int) : Intent
        object OnUpdate : Intent
        data class OnFastBooking(val minDuration: Int) : Intent
        data class OnUpdateSelectDate(val updateInDays: Int) : Intent
        object OnResetSelectDate : Intent
        data class OnShowToast(val text: String) : Intent
    }

    sealed interface Label {
        data class ShowToast(val text: String) : Label
    }

    data class State(
        val isLoad: Boolean,
        val isData: Boolean,
        val isError: Boolean,
        val isDisconnect: Boolean,
        val updatedEvent: EventInfo,
        val isSettings: Boolean,
        val roomList: List<RoomInfo>,
        val indexSelectRoom: Int,
        val timeToNextEvent: Int,
        val selectDate: Calendar,
        val showToast: String
    ) {
        companion object {
            val defaultState =
                State(
                    isLoad = true,
                    isData = false,
                    isError = false,
                    isDisconnect = false,
                    isSettings = false,
                    updatedEvent = EventInfo.emptyEvent,
                    roomList = listOf(),
                    indexSelectRoom = 0,
                    timeToNextEvent = 0,
                    selectDate = GregorianCalendar(),
                    showToast = ""
                )
        }
    }
}