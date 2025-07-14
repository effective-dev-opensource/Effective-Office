package band.effective.office.tablet.feature.bookingEditor.presentation.mapper

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import band.effective.office.tablet.feature.bookingEditor.presentation.State
import kotlin.time.Duration.Companion.minutes

class UpdateEventComponentStateToEventInfoMapper {

    fun map(state: State): EventInfo = EventInfo(
        startTime = state.date,
        finishTime = state.date.asInstant.plus(state.duration.minutes).asLocalDateTime,
        organizer = state.selectOrganizer,
        id = state.event.id,
        isLoading = false,
    )
}