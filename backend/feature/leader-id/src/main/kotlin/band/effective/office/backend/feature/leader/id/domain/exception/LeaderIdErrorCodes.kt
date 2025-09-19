package band.effective.office.backend.feature.leader.id.domain.exception

/**
 * Error codes for the LeaderId service.
 * These constants define the error codes that can be returned by the LeaderId service.
 */
object LeaderIdErrorCodes {
    // Resource not found errors (1xx)
    const val EVENT_NOT_FOUND = 101
    const val EVENTS_NOT_FOUND = 102

    // API / external service errors (2xx)
    const val API_UNAVAILABLE = 201
    const val API_TIMEOUT = 202
    const val API_AUTHENTICATION_FAILED = 203
    const val API_RATE_LIMIT_EXCEEDED = 204

    // Validation errors (3xx)
    const val INVALID_SEARCH_CRITERIA = 301
    const val INVALID_EVENT_ID = 302

    // Data processing errors (4xx)
    const val DATA_MAPPING_FAILED = 401
    const val EVENTS_RETRIEVAL_FAILED = 402
    const val EVENTS_SEARCH_FAILED = 403
}
