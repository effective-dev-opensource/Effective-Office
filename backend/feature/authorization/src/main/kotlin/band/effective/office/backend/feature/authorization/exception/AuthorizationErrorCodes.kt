package band.effective.office.backend.feature.authorization.exception

/**
 * Error codes for the authorization service.
 * These constants define the error codes that can be returned by the authorization service.
 */
object AuthorizationErrorCodes {
    // Authentication errors (3xx)
    const val INVALID_TOKEN = 301
    const val EXPIRED_TOKEN = 302
    const val MISSING_TOKEN = 303
    const val INVALID_API_KEY = 304
    const val UNAUTHORIZED = 305
    
    // Server errors (4xx)
    const val AUTHORIZATION_SERVER_ERROR = 401
}