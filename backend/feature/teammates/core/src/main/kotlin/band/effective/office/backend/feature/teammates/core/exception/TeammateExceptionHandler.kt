package band.effective.office.backend.feature.teammates.core.exception

import band.effective.office.backend.core.data.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Exception handler for teammates feature exceptions.
 */
@RestControllerAdvice
class TeammateExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(TeammateProviderUnavailableException::class)
    fun handleProviderUnavailable(ex: TeammateProviderUnavailableException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(InvalidTeammateRequestException::class)
    fun handleInvalidRequest(ex: InvalidTeammateRequestException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(TeammatesRetrievalFailedException::class)
    fun handleRetrievalFailed(ex: TeammatesRetrievalFailedException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(TeammatesCountFailedException::class)
    fun handleCountFailed(ex: TeammatesCountFailedException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(TeammatePropertyRetrievalFailedException::class)
    fun handlePropertyRetrievalFailed(ex: TeammatePropertyRetrievalFailedException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    /** Generic handler for TeammateException. */
    @ExceptionHandler(TeammateException::class)
    fun handleTeammateException(ex: TeammateException): ResponseEntity<ErrorDto> {
        logger.error("Teammate exception: {}", ex.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )
    }
}