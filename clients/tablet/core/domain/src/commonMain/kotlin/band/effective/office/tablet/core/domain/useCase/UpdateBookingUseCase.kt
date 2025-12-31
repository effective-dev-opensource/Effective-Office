package band.effective.office.tablet.core.domain.useCase

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository

/**
 * Use case for updating an existing booking in a room.
 *
 * @property networkBookingRepository Repository for network booking operations
 * @property localBookingRepository Repository for local booking storage operations
 */
class UpdateBookingUseCase(
    private val networkBookingRepository: BookingRepository,
    private val localBookingRepository: LocalBookingRepository,
    private val getRoomByNameUseCase: GetRoomByNameUseCase,
) {
    /**
     * Updates an existing booking in the specified room.
     * Updates the local repository immediately with a loading state,
     * then attempts to update the booking in the network repository.
     * If the network operation fails, the original event is restored in the local repository.
     *
     * @param roomName Name of the room where the booking exists
     * @param eventInfo Updated information about the event
     * @param roomInfo Information about the room where the booking exists
     * @return Either containing the updated event information or an error
     */
    suspend operator fun invoke(
        roomName: String,
        eventInfo: EventInfo,
    ): Either<ErrorResponse, EventInfo> {
        val roomInfo = getRoomByNameUseCase(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))
        val loadingEvent = eventInfo.copy(isLoading = true)

        // Get the original event to restore in case of failure
        val oldEvent = localBookingRepository.getBooking(eventInfo) as? Either.Success
            ?: return Either.Error(ErrorResponse(404, "Old event with id ${eventInfo.id} wasn't found"))

        // Update local repository with loading state
        localBookingRepository.updateBooking(loadingEvent, roomInfo)

        // Attempt to update booking in network repository
        val response = networkBookingRepository.updateBooking(loadingEvent, roomInfo)

        when (response) {
            is Either.Error -> {
                // On error, restore the original event in local repository
                localBookingRepository.updateBooking(oldEvent.data, roomInfo)
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
