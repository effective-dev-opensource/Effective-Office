package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository

/**
 * Use case for deleting an existing booking in a room.
 * 
 * @property networkBookingRepository Repository for network booking operations
 * @property localBookingRepository Repository for local booking storage operations
 */
class DeleteBookingUseCase(
    private val networkBookingRepository: BookingRepository,
    private val localBookingRepository: LocalBookingRepository,
    private val getRoomByNameUseCase: GetRoomByNameUseCase,
) {
    /**
     * Deletes an existing booking in the specified room.
     * Updates the local repository immediately with a loading state,
     * then attempts to delete the booking in the network repository.
     * If the network operation fails, the original event is restored in the local repository.
     * 
     * @param roomName Name of the room where the booking exists
     * @param eventInfo Information about the event to delete
     * @param roomInfo Information about the room where the booking exists
     * @return Either containing a success message or an error
     */
    suspend operator fun invoke(
        roomName: String,
        eventInfo: EventInfo,
    ): Either<ErrorResponse, String> {
        val roomInfo = getRoomByNameUseCase(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))
        val loadingEvent = eventInfo.copy(isLoading = true)

        // Save the original event state before attempting to delete
        val originalEvent = eventInfo.copy()

        // Mark as loading in local repository
        localBookingRepository.updateBooking(loadingEvent, roomInfo)

        // Attempt to delete from network
        val response = networkBookingRepository.deleteBooking(loadingEvent, roomInfo)

        when (response) {
            is Either.Error -> {
                // On error, restore the original event in local repository
                localBookingRepository.updateBooking(originalEvent, roomInfo)
            }

            is Either.Success -> {
                // On success, delete from local repository
                localBookingRepository.deleteBooking(loadingEvent, roomInfo)
            }
        }

        return response
    }
}
