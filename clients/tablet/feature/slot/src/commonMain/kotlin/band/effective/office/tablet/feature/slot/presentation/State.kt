package band.effective.office.tablet.feature.slot.presentation

import androidx.compose.runtime.Stable

@Stable
 data class State(
    val slots: List<SlotUi>
) {
    companion object {
        val initValue = State(slots = listOf())
    }
}