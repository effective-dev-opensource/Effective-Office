package band.effective.office.tablet.core.domain.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo

/**
 * Mediator interface for coordinating operations between network and local repositories.
 * This interface abstracts the coordination logic, reducing tight coupling between repositories.
 */
interface EventRepositoryMediator {
    /**
     * Synchronizes data between network and local repositories.
     * @return Either containing the synchronized room information or an error with saved data
     */
    suspend fun synchronizeData(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>
    
    /**
     * Handles booking creation, coordinating between network and local repositories.
     * @param eventInfo Information about the event to create
     * @param room Information about the room to book
     * @return Either containing the created event information or an error
     */
    suspend fun handleBookingCreation(
        eventInfo: EventInfo, 
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo>
    
    /**
     * Handles booking update, coordinating between network and local repositories.
     * @param eventInfo Updated information about the event
     * @param room Information about the room where the booking exists
     * @return Either containing the updated event information or an error
     */
    suspend fun handleBookingUpdate(
        eventInfo: EventInfo, 
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo>
    
    /**
     * Handles booking deletion, coordinating between network and local repositories.
     * @param eventInfo Information about the event to delete
     * @param room Information about the room where the booking exists
     * @return Either containing a success message or an error
     */
    suspend fun handleBookingDeletion(
        eventInfo: EventInfo, 
        room: RoomInfo
    ): Either<ErrorResponse, String>
}