package band.effective.office.tablet.feature.main

sealed interface Label {
    data class ShowToast(val text: String) : Label
}
