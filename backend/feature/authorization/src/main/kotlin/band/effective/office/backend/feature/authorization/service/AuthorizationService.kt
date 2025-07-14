package band.effective.office.backend.feature.authorization.service

import band.effective.office.backend.core.data.ErrorDto
import band.effective.office.backend.feature.authorization.core.AuthorizationChain
import band.effective.office.backend.feature.authorization.core.AuthorizationResult
import band.effective.office.backend.feature.authorization.exception.AuthorizationException
import band.effective.office.backend.feature.authorization.exception.AuthorizationErrorCodes
import band.effective.office.backend.feature.authorization.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Service that handles authorization using the authorization chain.
 */
@Service
class AuthorizationService(
    private val authorizationChain: AuthorizationChain
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Attempts to authorize the request using the authorization chain.
     *
     * @param request The HTTP request to authorize
     * @return The authorization result
     * @throws UnauthorizedException if authorization fails
     */
    fun authorize(request: HttpServletRequest): AuthorizationResult {
        logger.debug("Attempting to authorize request: {}", request.requestURI)

        // Use the authorization chain to authorize the request
        val result = authorizationChain.authorize(request)

        if (result.success) {
            logger.debug("Request authorized successfully")
            return result
        } else {
            logger.debug("Request not authorized: {}", result.errorMessage)

            // If there's an error code and message, throw a specific exception
            if (result.errorCode != null) {
                throw UnauthorizedException(result.errorMessage ?: "Unauthorized")
            }

            // Otherwise, throw a generic unauthorized exception
            throw UnauthorizedException("Unauthorized")
        }
    }

}
