package band.effective.office.backend.feature.sport.core.exception

/**
 * Error codes for the sport feature.
 * These constants define the error codes that can be returned by the sport feature.
 */
object SportErrorCodes {
    // Provider / infrastructure errors (4xx)
    const val PROVIDER_UNAVAILABLE = 401

    // Retrieval / IO errors (5xx)
    const val RETRIEVAL_FAILED = 501

    // Validation errors (6xx)
    const val INVALID_REQUEST = 601
}
