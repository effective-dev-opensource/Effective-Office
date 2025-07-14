package band.effective.office.backend.feature.authorization.exception

import band.effective.office.backend.core.data.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Global exception handler for authorization exceptions.
 * This class handles all exceptions thrown by the authorization feature.
 */
@ControllerAdvice
class AuthorizationExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * Exception handler for InvalidTokenException.
     */
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<ErrorDto> {
        logger.error("Invalid token: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
    
    /**
     * Exception handler for ExpiredTokenException.
     */
    @ExceptionHandler(ExpiredTokenException::class)
    fun handleExpiredTokenException(ex: ExpiredTokenException): ResponseEntity<ErrorDto> {
        logger.error("Expired token: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
    
    /**
     * Exception handler for MissingTokenException.
     */
    @ExceptionHandler(MissingTokenException::class)
    fun handleMissingTokenException(ex: MissingTokenException): ResponseEntity<ErrorDto> {
        logger.error("Missing token: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
    
    /**
     * Exception handler for InvalidApiKeyException.
     */
    @ExceptionHandler(InvalidApiKeyException::class)
    fun handleInvalidApiKeyException(ex: InvalidApiKeyException): ResponseEntity<ErrorDto> {
        logger.error("Invalid API key: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
    
    /**
     * Exception handler for UnauthorizedException.
     */
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex: UnauthorizedException): ResponseEntity<ErrorDto> {
        logger.error("Unauthorized: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
    
    /**
     * Generic exception handler for AuthorizationException.
     * This handler catches any AuthorizationException that doesn't have a more specific handler.
     */
    @ExceptionHandler(AuthorizationException::class)
    fun handleAuthorizationException(ex: AuthorizationException): ResponseEntity<ErrorDto> {
        logger.error("Authorization exception: {}", ex.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
}