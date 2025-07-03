package band.effective.office.tablet.feature.slot.presentation

import band.effective.office.tablet.core.domain.model.Slot
import kotlinx.datetime.LocalDate

sealed interface SlotIntent {
    data class ClickOnSlot(val slot: SlotUi) : SlotIntent
    data class UpdateRequest(val room: String, val refresh: Boolean = true) : SlotIntent
    data class UpdateDate(val newDate: LocalDate) : SlotIntent
    data class Delete(val slot: Slot, val onDelete: () -> Unit) : SlotIntent
    data class OnCancelDelete(val slot: SlotUi.DeleteSlot) : SlotIntent
}

