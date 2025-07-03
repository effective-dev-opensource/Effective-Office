package band.effective.office.tablet.feature.main.presentation.updateEvent

import band.effective.office.tablet.core.domain.model.Organizer
import kotlinx.datetime.LocalDateTime

sealed interface Intent {
    data class OnUpdateLength(val update: Int) : Intent
    data class OnUpdateDate(val updateInDays: Int) : Intent
    data class OnSetDate(val calendar: LocalDateTime) : Intent

    data object OnExpandedChange : Intent
    data class OnSelectOrganizer(val newOrganizer: Organizer) : Intent
    data class OnUpdateEvent(val room: String) : Intent
    data object OnDeleteEvent : Intent
    data class OnInput(val input: String) : Intent
    data object OnDoneInput : Intent
    data object OnOpenSelectDateDialog : Intent
    data object OnCloseSelectDateDialog : Intent
    data object OnClose : Intent
    data object OnBooking : Intent
}