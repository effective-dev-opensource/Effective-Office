package band.effective.office.client.core.data.api

import band.effective.office.client.core.data.dto.SuccessResponse
import band.effective.office.client.core.data.dto.booking.BookingRequestDTO
import band.effective.office.client.core.data.dto.booking.BookingResponseDTO
import band.effective.office.client.core.data.model.Either
import band.effective.office.client.core.data.model.ErrorResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Interface for booking-related operations
 */
interface BookingApi {
    /**
     * Get booking by id
     * @param id booking's id
     * @return Information about booking
     */
    suspend fun getBooking(id: String): Either<ErrorResponse, BookingResponseDTO>

    /**
     * Get user's bookings
     * @param userId user id whose booking need get
     * @param beginDate start date for bookings search
     * @param endDate end date for bookings search
     * @return list bookings current user
     */
    suspend fun getBookingsByUser(
        userId: String,
        beginDate: Long,
        endDate: Long
    ): Either<ErrorResponse, List<BookingResponseDTO>>

    /**
     * Get bookings in workspace
     * @param workspaceId workspace id to be reserved
     * @param from optional start date for bookings search
     * @param to optional end date for bookings search
     * @return list bookings in current workspace
     */
    suspend fun getBookingsByWorkspaces(
        workspaceId: String,
        from: Long? = null,
        to: Long? = null
    ): Either<ErrorResponse, List<BookingResponseDTO>>

    /**
     * Booking workspace
     * @param bookingInfo information for booking workspace
     * @return Created entry from database
     */
    suspend fun createBooking(
        bookingInfo: BookingRequestDTO
    ): Either<ErrorResponse, BookingResponseDTO>

    /**
     * Update booking info
     * @param bookingInfo new information about booking
     * @param bookingId id of booking to be updated
     * @return new entry from database
     */
    suspend fun updateBooking(
        bookingInfo: BookingRequestDTO,
        bookingId: String,
    ): Either<ErrorResponse, BookingResponseDTO>

    /**
     * Delete booking
     * @param bookingId id of booking to be deleted
     * @return If complete return SuccessResponse, else return ErrorResponse
     */
    suspend fun deleteBooking(
        bookingId: String
    ): Either<ErrorResponse, SuccessResponse>

    /**
     * Subscribe on bookings list updates
     * @param workspaceId workspace name
     * @param scope CoroutineScope for collect updates
     * @return Flow with updates
     */
    fun subscribeOnBookingsList(
        workspaceId: String,
        scope: CoroutineScope
    ): Flow<Either<ErrorResponse, List<BookingResponseDTO>>>

    /**
     * Get all bookings in a date range
     * @param rangeFrom optional start date for bookings search
     * @param rangeTo optional end date for bookings search
     * @return list of all bookings in the date range
     */
    suspend fun getBookings(
        rangeFrom: Long? = null,
        rangeTo: Long? = null
    ): Either<ErrorResponse, List<BookingResponseDTO>>
}