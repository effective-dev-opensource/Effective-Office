package band.effective.office.tablet.feature.fastBooking.presentation

sealed interface Intent {
    data class OnFreeSelectRequest(val room: String) : Intent
    object OnCloseWindowRequest : Intent
}