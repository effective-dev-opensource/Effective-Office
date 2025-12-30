package band.effective.office.backend.feature.teammates.core.exception

/**
 * Error codes for the teammates feature.
 * These constants define the error codes that can be returned by the teammates feature.
 */
object TeammateErrorCodes {
    // Provider / infrastructure errors (4xx)
    const val PROVIDER_UNAVAILABLE = 401

    // Retrieval / IO errors (5xx)
    const val RETRIEVAL_FAILED = 501
    const val COUNT_FAILED = 502
    const val PROPERTY_RETRIEVAL_FAILED = 503

    // Validation errors (6xx)
    const val INVALID_REQUEST = 601
}