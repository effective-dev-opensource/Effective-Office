    package band.effective.office.backend.feature.photos.core.exception

import band.effective.office.backend.core.data.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Exception handler for photos feature exceptions.
 */
@RestControllerAdvice
class PhotosExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(PhotoProviderUnavailableException::class)
    fun handleProviderUnavailable(ex: PhotoProviderUnavailableException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(InvalidPhotoRequestException::class)
    fun handleInvalidRequest(ex: InvalidPhotoRequestException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(PhotosRetrievalFailedException::class)
    fun handleRetrievalFailed(ex: PhotosRetrievalFailedException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(PhotosCountFailedException::class)
    fun handleCountFailed(ex: PhotosCountFailedException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    /** Generic handler for PhotosException. */
    @ExceptionHandler(PhotosException::class)
    fun handlePhotosException(ex: PhotosException): ResponseEntity<ErrorDto> {
        logger.error("Photos exception: {}", ex.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )
    }
}