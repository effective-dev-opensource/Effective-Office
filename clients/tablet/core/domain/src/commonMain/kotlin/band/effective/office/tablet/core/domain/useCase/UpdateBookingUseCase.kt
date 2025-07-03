package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.repository.EventManagerRepository

class UpdateBookingUseCase(
    private val eventManagerRepository: EventManagerRepository,
) {
    suspend operator fun invoke(
        roomName: String,
        eventInfo: EventInfo,
    ) = eventManagerRepository.updateBooking(
        roomName = roomName,
        eventInfo = eventInfo,
    )
}