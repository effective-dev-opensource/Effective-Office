package band.effective.office.tablet.feature.slot.presentation

import band.effective.office.tablet.core.domain.model.Slot
import kotlinx.datetime.LocalDateTime

sealed interface SlotIntent {
    data class ClickToEdit(val slot: SlotUi) : SlotIntent
    data class ClickToToggle(val slot: SlotUi.MultiSlot) : SlotIntent
    data class UpdateDate(val newDate: LocalDateTime) : SlotIntent
    data class UpdateRequest(val room: String) : SlotIntent
    data class Delete(val slot: Slot, val onDelete: () -> Unit) : SlotIntent
    data class OnCancelDelete(val slot: SlotUi.DeleteSlot) : SlotIntent
}

