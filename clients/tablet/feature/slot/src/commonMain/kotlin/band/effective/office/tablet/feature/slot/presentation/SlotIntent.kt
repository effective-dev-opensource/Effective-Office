package band.effective.office.tablet.feature.slot.presentation

import kotlinx.datetime.LocalDateTime

sealed interface SlotIntent {
    data class ClickToEdit(val slot: SlotUi) : SlotIntent
    data class ClickToToggle(val slot: SlotUi.MultiSlot) : SlotIntent
    data class UpdateRequest(val room: String, val newDate: LocalDateTime) : SlotIntent
    data object InactivityTimeout: SlotIntent
}

