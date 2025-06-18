package band.effective.office.backend.feature.booking.core.exception

import band.effective.office.backend.feature.booking.core.exception.BookingErrorCodes

/**
 * Base exception class for booking service exceptions.
 * All booking service exceptions should extend this class.
 *
 * @property message Detailed description of the error
 * @property errorCode Error code (not HTTP status code)
 */
open class BookingException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)

/**
 * Exception thrown when a booking is not found.
 */
class BookingNotFoundException(
    message: String = "Booking not found"
) : BookingException(message, BookingErrorCodes.BOOKING_NOT_FOUND)

/**
 * Exception thrown when a user is not found.
 */
class UserNotFoundException(
    message: String = "User not found"
) : BookingException(message, BookingErrorCodes.USER_NOT_FOUND)

/**
 * Exception thrown when a workspace is not found.
 */
class WorkspaceNotFoundException(
    message: String = "Workspace not found"
) : BookingException(message, BookingErrorCodes.WORKSPACE_NOT_FOUND)

/**
 * Exception thrown when a time range is invalid.
 */
class InvalidTimeRangeException(
    message: String = "Invalid time range"
) : BookingException(message, BookingErrorCodes.INVALID_TIME_RANGE)

/**
 * Exception thrown when a booking overlaps with an existing booking.
 */
class OverlappingBookingException(
    message: String = "Booking overlaps with an existing booking"
) : BookingException(message, BookingErrorCodes.OVERLAPPING_BOOKING)
