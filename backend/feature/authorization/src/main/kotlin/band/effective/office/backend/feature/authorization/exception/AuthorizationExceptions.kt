package band.effective.office.backend.feature.authorization.exception

import band.effective.office.backend.feature.authorization.exception.AuthorizationErrorCodes

/**
 * Base exception class for authorization service exceptions.
 * All authorization service exceptions should extend this class.
 *
 * @property message Detailed description of the error
 * @property errorCode Error code (not HTTP status code)
 */
open class AuthorizationException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)

/**
 * Exception thrown when a token is invalid.
 */
class InvalidTokenException(
    message: String = "Invalid token"
) : AuthorizationException(message, AuthorizationErrorCodes.INVALID_TOKEN)

/**
 * Exception thrown when a token has expired.
 */
class ExpiredTokenException(
    message: String = "Token has expired"
) : AuthorizationException(message, AuthorizationErrorCodes.EXPIRED_TOKEN)

/**
 * Exception thrown when a token is missing.
 */
class MissingTokenException(
    message: String = "Missing token"
) : AuthorizationException(message, AuthorizationErrorCodes.MISSING_TOKEN)

/**
 * Exception thrown when an API key is invalid.
 */
class InvalidApiKeyException(
    message: String = "Invalid API key"
) : AuthorizationException(message, AuthorizationErrorCodes.INVALID_API_KEY)

/**
 * Exception thrown when a request is unauthorized.
 */
class UnauthorizedException(
    message: String = "Unauthorized"
) : AuthorizationException(message, AuthorizationErrorCodes.UNAUTHORIZED)