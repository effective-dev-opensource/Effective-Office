package band.effective.office.tablet.feature.main.domain

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.useCase.SlotUseCase
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.domain.util.roundUpToNextQuarter
import kotlinx.datetime.LocalDateTime

class GetSlotsByRoomUseCase(
    private val slotUseCase: SlotUseCase,
) {

    operator fun invoke(
        roomInfo: RoomInfo,
        start: LocalDateTime = currentLocalDateTime,
        finish: LocalDateTime = OfficeTime.finishWorkTime(start)
    ): List<Slot> {
        val roundedStart = roundUpToNextQuarter(start)
        return slotUseCase.getSlots(
            currentEvent = roomInfo.currentEvent,
            events = roomInfo.eventList,
            start = roundedStart,
            finish = finish
        )
    }
}