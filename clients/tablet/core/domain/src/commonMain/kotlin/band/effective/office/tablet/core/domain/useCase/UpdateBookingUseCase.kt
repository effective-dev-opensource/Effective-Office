package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import io.github.aakira.napier.Napier

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
        Napier.d { "[UpdateBookingUseCase] Starting update for booking, room=$roomName, eventId=${eventInfo.id}" }
        val roomInfo = getRoomByNameUseCase(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName")).also {
                Napier.e { "[UpdateBookingUseCase] Room not found: $roomName" }
            }
        val loadingEvent = eventInfo.copy(isLoading = true)

        // Get the original event to restore in case of failure
        val oldEvent = localBookingRepository.getBooking(eventInfo) as? Either.Success
            ?: return Either.Error(ErrorResponse(404, "Old event with id ${eventInfo.id} wasn't found")).also {
                Napier.e { "[UpdateBookingUseCase] Old event not found: eventId=${eventInfo.id}" }
            }

        // Update local repository with loading state
        localBookingRepository.updateBooking(loadingEvent, roomInfo)
            .also { Napier.d { "[UpdateBookingUseCase] Updated local repository with loading state for eventId=${loadingEvent.id}" } }

        // Attempt to update booking in network repository
        val response = networkBookingRepository.updateBooking(loadingEvent, roomInfo)
            .also { result ->
                when (result) {
                    is Either.Error -> Napier.e { "[UpdateBookingUseCase] Failed to update booking: room=$roomName, eventId=${eventInfo.id}, code=${result.error.code}, description=${result.error.description}" }
                    is Either.Success -> Napier.i { "[UpdateBookingUseCase] Successfully updated booking: room=$roomName, eventId=${result.data.id}" }
                }
            }

        when (response) {
            is Either.Error -> {
                // On error, restore the original event in local repository
                localBookingRepository.updateBooking(oldEvent.data, roomInfo)
                    .also { Napier.d { "[UpdateBookingUseCase] Restore original event in local repository: eventId=${oldEvent.data.id}" } }
            }

            is Either.Success -> {
                // On success, update the booking in local repository with the response data
                val event = response.data
                localBookingRepository.updateBooking(event, roomInfo)
                    .also { Napier.d { "[UpdateBookingUseCase] Updated local repository with new event: eventId=${event.id}" } }
            }
        }

        return response
    }
}
