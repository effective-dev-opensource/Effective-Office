package band.effective.office.tablet.feature.bookingEditor.presentation

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import kotlinx.datetime.LocalDateTime

data class State(
    val duration: Int,
    val date: LocalDateTime,
    val organizers: List<Organizer>,
    val selectOrganizers: List<Organizer>,
    val selectOrganizer: Organizer,
    val expanded: Boolean,
    val event: EventInfo,
    val inputText: String,
    val isInputError: Boolean,
    val isLoadDelete: Boolean,
    val isErrorDelete: Boolean,
    val isLoadUpdate: Boolean,
    val isErrorUpdate: Boolean,
    val isLoadCreate: Boolean,
    val isErrorCreate: Boolean,
    val showSelectDate: Boolean,
    val enableUpdateButton: Boolean,
    val isBusyEvent: Boolean
) {
    companion object {
        val defaultValue = State(
            duration = 30,
            date = currentLocalDateTime,
            organizers = listOf(),
            selectOrganizers = listOf(),
            selectOrganizer = Organizer.default,
            expanded = false,
            event = EventInfo.emptyEvent,
            inputText = "",
            isInputError = false,
            isLoadDelete = false,
            isErrorDelete = false,
            isLoadUpdate = false,
            isErrorUpdate = false,
            isLoadCreate = false,
            isErrorCreate = false,
            showSelectDate = false,
            enableUpdateButton = false,
            isBusyEvent = false
        )
    }

    fun isCreatedEvent() = !event.isNotCreated()
}
