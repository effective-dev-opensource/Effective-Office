package band.effective.office.tablet.feature.main.presentation.main

sealed interface Intent {
    object OnOpenFreeRoomModal : Intent
    object RebootRequest : Intent
    data class OnChangeEventRequest(val eventInfo: Any) : Intent
    data class OnSelectRoom(val index: Int) : Intent
    object OnUpdate : Intent
    data class OnFastBooking(val minDuration: Int) : Intent
    data class OnUpdateSelectDate(val updateInDays: Int) : Intent
    object OnResetSelectDate : Intent
}