package band.effective.office.backend.feature.sport.core.exception

import band.effective.office.backend.core.data.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Exception handler for sport feature exceptions.
 */
@RestControllerAdvice
class SportExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(SportProviderUnavailableException::class)
    fun handleProviderUnavailable(ex: SportProviderUnavailableException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(InvalidSportRequestException::class)
    fun handleInvalidRequest(ex: InvalidSportRequestException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(SportUsersRetrievalFailedException::class)
    fun handleRetrievalFailed(ex: SportUsersRetrievalFailedException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    /** Generic handler for SportException. */
    @ExceptionHandler(SportException::class)
    fun handleSportException(ex: SportException): ResponseEntity<ErrorDto> {
        logger.error("Sport exception: {}", ex.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )
    }
}
