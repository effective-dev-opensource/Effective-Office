package band.effective.office.tablet.core.domain.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo

/**
 * Repository interface for local booking operations.
 * Extends [BookingRepository] to provide additional methods for local storage operations.
 */
interface LocalBookingRepository {
    /**
     * Creates a new booking in the specified room.
     *
     * @param eventInfo Information about the event to create
     * @param room Information about the room to book
     * @return Either containing the created event information or an error
     */
    suspend fun createBooking(eventInfo: EventInfo, room: RoomInfo): Either<ErrorResponse, EventInfo>

    /**
     * Updates an existing booking in the specified room.
     *
     * @param eventInfo Updated information about the event
     * @param room Information about the room where the booking exists
     * @return Either containing the updated event information or an error
     */
    suspend fun updateBooking(eventInfo: EventInfo, room: RoomInfo): Either<ErrorResponse, EventInfo>

    /**
     * Deletes an existing booking in the specified room.
     *
     * @param eventInfo Information about the event to delete
     * @param room Information about the room where the booking exists
     * @return Either containing a success message or an error
     */
    suspend fun deleteBooking(eventInfo: EventInfo, room: RoomInfo): Either<ErrorResponse, String>

    /**
     * Gets information about a specific booking.
     *
     * @param eventInfo Information about the event to retrieve
     * @return Either containing the event information or an error
     */
    suspend fun getBooking(eventInfo: EventInfo): Either<ErrorResponse, EventInfo>
}
