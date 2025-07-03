package band.effective.office.tablet.feature.main.domain

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.repository.EventManagerRepository

class CreateBookingUseCase(
    private val eventManagerRepository: EventManagerRepository,
) {
    suspend operator fun invoke(roomName: String, eventInfo: EventInfo) =
        eventManagerRepository.createBooking(
            roomName = roomName,
            eventInfo = eventInfo,
        )
}