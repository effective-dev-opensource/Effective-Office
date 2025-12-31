package band.effective.office.tablet.feature.bookingEditor.presentation.mapper

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.shared.core.utils.asInstant
import band.effective.office.tablet.feature.bookingEditor.presentation.State

class EventInfoMapper {

    fun mapToSlot(eventInfo: EventInfo): Slot =
        Slot.EventSlot(start = eventInfo.startTime, finish = eventInfo.finishTime, eventInfo = eventInfo)

    fun mapToUpdateBookingState(eventInfo: EventInfo): State {
        val duration = eventInfo.finishTime.asInstant.minus(eventInfo.startTime.asInstant).inWholeMinutes.toInt()
        val finishWorkTime = OfficeTime.finishWorkTime(eventInfo.startTime.date)
        val canIncrementDuration = eventInfo.finishTime < finishWorkTime
        return State.defaultValue.copy(
            date = eventInfo.startTime,
            duration = duration,
            selectOrganizer = eventInfo.organizer,
            inputText = eventInfo.organizer.fullName,
            event = eventInfo,
            canIncrementDuration = canIncrementDuration
        )
    }
}