package band.effective.office.tablet.feature.main.presentation.freeuproom

sealed interface Intent {
    object OnFreeSelectRequest : Intent
    object OnCloseWindowRequest : Intent
}

