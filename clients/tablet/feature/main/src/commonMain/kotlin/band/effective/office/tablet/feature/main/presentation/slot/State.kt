package band.effective.office.tablet.feature.main.presentation.slot

data class State(
    val slots: List<SlotUi>
) {
    companion object {
        val initValue = State(slots = listOf())
    }
}