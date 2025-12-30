package band.effective.office.backend.feature.sport.core.exception

/**
 * Base exception class for sport feature exceptions.
 * All sport feature exceptions should extend this class.
 *
 * @property message Detailed description of the error
 * @property errorCode Error code (not HTTP status code)
 */
open class SportException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)

/**
 * Exception thrown when sport provider is misconfigured or unavailable.
 */
class SportProviderUnavailableException(
    message: String = "Sport provider is unavailable"
) : SportException(message, SportErrorCodes.PROVIDER_UNAVAILABLE)

/**
 * Exception thrown when sport users retrieval fails for any reason.
 */
class SportUsersRetrievalFailedException(
    message: String = "Failed to retrieve sport users"
) : SportException(message, SportErrorCodes.RETRIEVAL_FAILED)

/**
 * Exception thrown when input parameters are invalid.
 */
class InvalidSportRequestException(
    message: String = "Invalid sport request"
) : SportException(message, SportErrorCodes.INVALID_REQUEST)
