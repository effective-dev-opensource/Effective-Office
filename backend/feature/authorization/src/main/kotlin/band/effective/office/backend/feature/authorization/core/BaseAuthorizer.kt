package band.effective.office.backend.feature.authorization.core

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory

/**
 * Base implementation of the Authorizer interface.
 * Provides common functionality for all authorizers in the chain.
 */
abstract class BaseAuthorizer : Authorizer {
    protected val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * The next authorizer in the chain.
     */
    protected var nextAuthorizer: Authorizer? = null

    /**
     * Sets the next authorizer in the chain.
     *
     * @param next The next authorizer to process the request if this one fails
     * @return The next authorizer
     */
    override fun setNext(next: Authorizer): Authorizer {
        nextAuthorizer = next
        return next
    }

    /**
     * Attempts to authorize the request.
     * If this authorizer fails, it passes the request to the next authorizer in the chain.
     *
     * @param request The HTTP request to authorize
     * @return The authorization result containing success status and optional error information
     */
    override fun authorize(request: HttpServletRequest): AuthorizationResult {
        try {
            val result = doAuthorize(request)

            if (result.success) {
                logger.debug("Authorization successful with {}", this::class.simpleName)
                return result
            }

            // If this authorizer failed and there's a next one, try it
            return nextAuthorizer?.authorize(request) ?: result
        } catch (ex: band.effective.office.backend.feature.authorization.exception.AuthorizationException) {
            logger.debug("Authorization failed with exception: {}", ex.message)
            return AuthorizationResult(
                success = false,
                errorMessage = ex.message,
                errorCode = ex.errorCode
            )
        } catch (ex: Exception) {
            logger.error("Unexpected error during authorization: {}", ex.message)
            return AuthorizationResult(
                success = false,
                errorMessage = "Internal server error during authorization",
                errorCode = band.effective.office.backend.feature.authorization.exception.AuthorizationErrorCodes.AUTHORIZATION_SERVER_ERROR
            )
        }
    }

    /**
     * Performs the actual authorization logic.
     * This method should be implemented by concrete authorizers.
     *
     * @param request The HTTP request to authorize
     * @return The authorization result
     */
    protected abstract fun doAuthorize(request: HttpServletRequest): AuthorizationResult
}
