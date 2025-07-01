package band.effective.office.tablet.feature.main.presentation.main

sealed interface Label {
    data class ShowToast(val text: String) : Label
}
