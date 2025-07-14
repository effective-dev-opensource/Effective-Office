package band.effective.office.backend.app.exception

import band.effective.office.backend.core.data.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handler for all unhandled exceptions.
 * This class catches any exception that isn't handled by more specific exception handlers
 * and returns a standardized error response with a 500 status code.
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * Exception handler for all unhandled exceptions.
     * Returns a 500 Internal Server Error with a standardized error response.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorDto> {
        logger.error("Unhandled exception: ", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(
                message = ex.message ?: "An unexpected error occurred",
                code = HttpStatus.INTERNAL_SERVER_ERROR.value()
            )
        )
    }
}