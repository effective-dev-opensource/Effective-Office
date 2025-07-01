package band.effective.office.tablet.feature.main.presentation.fastevent

sealed interface Intent {
    data class OnFreeSelectRequest(val room: String) : Intent
    object OnCloseWindowRequest : Intent
}