package band.effective.office.tablet.feature.main.presentation.slot

import band.effective.office.tablet.core.domain.model.Slot

sealed interface SlotUi {
    val slot: Slot

    data class SimpleSlot(override val slot: Slot) : SlotUi
    data class MultiSlot(override val slot: Slot, val subSlots: List<SlotUi>, val isOpen: Boolean) :
        SlotUi

    data class DeleteSlot(
        override val slot: Slot,
        val onDelete: () -> Unit,
        val original: SlotUi,
        val index: Int,
        val mainSlotIndex: Int?
    ) : SlotUi

    data class NestedSlot(override val slot: Slot) : SlotUi
    data class LoadingSlot(override val slot: Slot) : SlotUi
}
