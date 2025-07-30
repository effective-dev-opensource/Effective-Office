package band.effective.office.tablet.feature.slot.presentation

import band.effective.office.tablet.core.domain.model.Slot
import kotlinx.datetime.Clock

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
        val mainSlotIndex: Int?,
        val startTimeMillis: Long = Clock.System.now().toEpochMilliseconds(),
        val deletionProgress: Float = 100f
    ) : SlotUi

    data class NestedSlot(override val slot: Slot) : SlotUi
    data class LoadingSlot(override val slot: Slot) : SlotUi
}
