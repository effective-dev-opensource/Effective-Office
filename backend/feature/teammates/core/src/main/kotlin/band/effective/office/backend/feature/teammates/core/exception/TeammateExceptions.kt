package band.effective.office.backend.feature.teammates.core.exception

/**
 * Base exception class for teammates feature exceptions.
 * All teammates feature exceptions should extend this class.
 *
 * @property message Detailed description of the error
 * @property errorCode Error code (not HTTP status code)
 */
open class TeammateException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)

/**
 * Exception thrown when teammates provider is misconfigured or unavailable.
 */
class TeammateProviderUnavailableException(
    message: String = "Teammate provider is unavailable"
) : TeammateException(message, TeammateErrorCodes.PROVIDER_UNAVAILABLE)

/**
 * Exception thrown when teammates retrieval fails for any reason.
 */
class TeammatesRetrievalFailedException(
    message: String = "Failed to retrieve teammates"
) : TeammateException(message, TeammateErrorCodes.RETRIEVAL_FAILED)

/**
 * Exception thrown when teammates count retrieval fails.
 */
class TeammatesCountFailedException(
    message: String = "Failed to retrieve teammates count"
) : TeammateException(message, TeammateErrorCodes.COUNT_FAILED)

/**
 * Exception thrown when teammate property retrieval fails.
 */
class TeammatePropertyRetrievalFailedException(
    message: String = "Failed to retrieve teammate property"
) : TeammateException(message, TeammateErrorCodes.PROPERTY_RETRIEVAL_FAILED)

/**
 * Exception thrown when input parameters are invalid.
 */
class InvalidTeammateRequestException(
    message: String = "Invalid teammate request"
) : TeammateException(message, TeammateErrorCodes.INVALID_REQUEST)