package band.effective.office.backend.feature.booking.core.exception

/**
 * Error codes for the booking service.
 * These constants define the error codes that can be returned by the booking service.
 */
object BookingErrorCodes {
    // Resource not found errors (1xx)
    const val BOOKING_NOT_FOUND = 101
    const val USER_NOT_FOUND = 102
    const val WORKSPACE_NOT_FOUND = 103

    // Validation errors (2xx)
    const val INVALID_TIME_RANGE = 201
    const val OVERLAPPING_BOOKING = 202
}