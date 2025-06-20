package band.effective.office.backend.feature.authorization.core.exception

/**
 * Base class for all authorization-related exceptions.
 */
sealed class AuthorizationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * Exception thrown when authentication fails.
 */
class AuthenticationException(message: String, cause: Throwable? = null) : AuthorizationException(message, cause)

/**
 * Exception thrown when a token is invalid.
 */
class InvalidTokenException(message: String, cause: Throwable? = null) : AuthorizationException(message, cause)

/**
 * Exception thrown when a token has expired.
 */
class TokenExpiredException(message: String, cause: Throwable? = null) : AuthorizationException(message, cause)

/**
 * Exception thrown when a user does not have the required permissions.
 */
class AccessDeniedException(message: String, cause: Throwable? = null) : AuthorizationException(message, cause)