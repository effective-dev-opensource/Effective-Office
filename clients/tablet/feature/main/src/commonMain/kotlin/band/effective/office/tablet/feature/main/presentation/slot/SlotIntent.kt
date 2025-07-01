package band.effective.office.tablet.feature.main.presentation.slot

import band.effective.office.tablet.core.domain.model.Slot
import kotlinx.datetime.LocalDateTime

sealed interface SlotIntent {
    data class ClickOnSlot(val slot: SlotUi) : SlotIntent
    data class UpdateRequest(val room: String, val refresh: Boolean = true) : SlotIntent
    data class UpdateDate(val newDate: LocalDateTime) : SlotIntent
    data class Delete(val slot: Slot, val onDelete: () -> Unit) : SlotIntent
    data class OnCancelDelete(val slot: SlotUi.DeleteSlot) : SlotIntent
}

