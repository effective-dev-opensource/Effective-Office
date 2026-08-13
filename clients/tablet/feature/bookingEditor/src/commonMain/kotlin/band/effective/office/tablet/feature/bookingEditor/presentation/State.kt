package band.effective.office.tablet.feature.bookingEditor.presentation

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.shared.core.utils.currentLocalDateTime
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
    val isBusyEvent: Boolean,
    val isTimeInPastError: Boolean,
    val isFinishTimeExceeded: Boolean,
    val canIncrementDuration: Boolean
) {
    /**
     * Whether the booking may be saved. [isInputError] is deliberately not part of it — that one
     * only paints the organizer field red once the user has finished typing.
     */
    val enableUpdateButton: Boolean
        get() = organizers.contains(selectOrganizer) &&
            !isBusyEvent &&
            !isTimeInPastError &&
            !isFinishTimeExceeded

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
            isBusyEvent = false,
            isTimeInPastError = false,
            isFinishTimeExceeded = false,
            canIncrementDuration = true
        )
    }

    fun isCreatedEvent() = !event.isNotCreated()
}
