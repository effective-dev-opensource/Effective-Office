package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.EventRepositoryMediator
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import band.effective.office.tablet.core.domain.unbox

/**
 * Default implementation of [EventRepositoryMediator] that coordinates operations between
 * network and local repositories.
 *
 * @property networkRepository Repository for network operations
 * @property localRepository Repository for local storage operations
 */
class DefaultEventRepositoryMediator(
    private val networkRepository: BookingRepository,
    private val localRepository: LocalBookingRepository
) : EventRepositoryMediator {

    /**
     * Synchronizes data between network and local repositories.
     * Fetches data from the network and updates the local repository.
     * If the network operation fails, it will use the saved data from the local repository.
     * 
     * @return Either containing the synchronized room information or an error with saved data
     */
    override suspend fun synchronizeData(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val save = localRepository.getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        val roomInfos = networkRepository.getRoomsInfo()
            .map(
                errorMapper = { error -> 
                    // Prevent NPE by handling null save data
                    error.copy(saveData = save)
                },
                successMapper = { it }
            )
        localRepository.updateRoomsInfo(roomInfos)
        return roomInfos
    }

    /**
     * Handles booking creation, coordinating between network and local repositories.
     * Updates the local repository immediately with a loading state,
     * then attempts to create the booking in the network repository.
     * If the network operation fails, the booking is removed from the local repository.
     * 
     * @param eventInfo Information about the event to create
     * @param room Information about the room to book
     * @return Either containing the created event information or an error
     */
    override suspend fun handleBookingCreation(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> {
        val loadingEvent = eventInfo.copy(isLoading = true)
        
        // Update local repository with loading state
        localRepository.createBooking(loadingEvent, room)
        
        // Attempt to create booking in network repository
        val response = networkRepository.createBooking(loadingEvent, room)
        
        when (response) {
            is Either.Error -> {
                // On error, remove the booking from local repository
                localRepository.deleteBooking(loadingEvent, room)
            }
            is Either.Success -> {
                // On success, update the booking in local repository with the response data
                val event = response.data
                localRepository.updateBooking(event, room)
            }
        }
        
        return response
    }

    /**
     * Handles booking update, coordinating between network and local repositories.
     * Updates the local repository immediately with a loading state,
     * then attempts to update the booking in the network repository.
     * If the network operation fails, the original event is restored in the local repository.
     * 
     * @param eventInfo Updated information about the event
     * @param room Information about the room where the booking exists
     * @return Either containing the updated event information or an error
     */
    override suspend fun handleBookingUpdate(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> {
        val loadingEvent = eventInfo.copy(isLoading = true)
        
        // Get the original event to restore in case of failure
        val oldEvent = localRepository.getBooking(eventInfo) as? Either.Success
            ?: return Either.Error(ErrorResponse(404, "Old event with id ${eventInfo.id} wasn't found"))
        
        // Update local repository with loading state
        localRepository.updateBooking(loadingEvent, room)
        
        // Attempt to update booking in network repository
        val response = networkRepository.updateBooking(loadingEvent, room)
        
        when (response) {
            is Either.Error -> {
                // On error, restore the original event in local repository
                localRepository.updateBooking(oldEvent.data, room)
            }
            is Either.Success -> {
                // On success, update the booking in local repository with the response data
                val event = response.data
                localRepository.updateBooking(event, room)
            }
        }
        
        return response
    }

    /**
     * Handles booking deletion, coordinating between network and local repositories.
     * Updates the local repository immediately with a loading state,
     * then attempts to delete the booking in the network repository.
     * If the network operation fails, the original event is restored in the local repository.
     * 
     * @param eventInfo Information about the event to delete
     * @param room Information about the room where the booking exists
     * @return Either containing a success message or an error
     */
    override suspend fun handleBookingDeletion(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, String> {
        val loadingEvent = eventInfo.copy(isLoading = true)
        
        // Save the original event state before attempting to delete
        val originalEvent = eventInfo.copy()
        
        // Mark as loading in local repository
        localRepository.updateBooking(loadingEvent, room)
        
        // Attempt to delete from network
        val response = networkRepository.deleteBooking(loadingEvent, room)
        
        when (response) {
            is Either.Error -> {
                // On error, restore the original event in local repository
                localRepository.updateBooking(originalEvent, room)
            }
            is Either.Success -> {
                // On success, delete from local repository
                localRepository.deleteBooking(loadingEvent, room)
            }
        }
        
        return response
    }
}