package band.effective.office.tablet.feature.slot.presentation

data class State(
    val slots: List<SlotUi>
) {
    companion object {
        val initValue = State(slots = listOf())
    }
}