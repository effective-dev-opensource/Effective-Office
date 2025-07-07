package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository

/**
 * Use case for creating a new booking in a room.
 * 
 * @property networkBookingRepository Repository for network booking operations
 * @property localBookingRepository Repository for local booking storage operations
 */
class CreateBookingUseCase(
    private val networkBookingRepository: BookingRepository,
    private val localBookingRepository: LocalBookingRepository,
    private val getRoomByNameUseCase: GetRoomByNameUseCase,
) {
    /**
     * Creates a new booking in the specified room.
     * Updates the local repository immediately with a loading state,
     * then attempts to create the booking in the network repository.
     * If the network operation fails, the booking is removed from the local repository.
     * 
     * @param roomName Name of the room to book
     * @param eventInfo Information about the event to create
     * @param roomInfo Information about the room to book
     * @return Either containing the created event information or an error
     */
    suspend operator fun invoke(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        val roomInfo = getRoomByNameUseCase(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))

        val loadingEvent = eventInfo.copy(isLoading = true)

        // Update local repository with loading state
        localBookingRepository.createBooking(loadingEvent, roomInfo)

        // Attempt to create booking in network repository
        val response = networkBookingRepository.createBooking(loadingEvent, roomInfo)

        when (response) {
            is Either.Error -> {
                // On error, remove the booking from local repository
                localBookingRepository.deleteBooking(loadingEvent, roomInfo)
            }

            is Either.Success -> {
                // On success, update the booking in local repository with the response data
                val event = response.data
                localBookingRepository.updateBooking(event, roomInfo)
            }
        }

        return response
    }
}
