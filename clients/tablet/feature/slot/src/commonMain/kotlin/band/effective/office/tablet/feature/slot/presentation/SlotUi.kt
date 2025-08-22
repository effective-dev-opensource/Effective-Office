package band.effective.office.tablet.feature.slot.presentation

import androidx.compose.runtime.Stable
import band.effective.office.tablet.core.domain.model.Slot

@Stable
sealed interface SlotUi {
    val slot: Slot

    data class SimpleSlot(override val slot: Slot) : SlotUi
    data class MultiSlot(override val slot: Slot, val subSlots: List<SlotUi>, val isOpen: Boolean) :
        SlotUi

    data class NestedSlot(override val slot: Slot) : SlotUi
    data class LoadingSlot(override val slot: Slot) : SlotUi
}
