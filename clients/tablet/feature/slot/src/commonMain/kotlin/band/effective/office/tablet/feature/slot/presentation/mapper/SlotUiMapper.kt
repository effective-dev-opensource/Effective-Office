package band.effective.office.tablet.feature.slot.presentation.mapper

import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.feature.slot.presentation.SlotUi

class SlotUiMapper {

    fun map(slot: Slot): SlotUi = when (slot) {
        is Slot.EmptySlot -> SlotUi.SimpleSlot(slot)
        is Slot.EventSlot -> SlotUi.SimpleSlot(slot)
        is Slot.MultiEventSlot -> SlotUi.MultiSlot(
            slot = slot,
            subSlots = slot.events.map { slot -> SlotUi.NestedSlot(slot) },
            isOpen = false
        )

        is Slot.LoadingEventSlot -> SlotUi.LoadingSlot(slot)
    }
}