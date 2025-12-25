package band.effective.office.backend.feature.photo.saver.core.exception

import band.effective.office.backend.core.data.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Exception handler for photo saver feature exceptions.
 */
@RestControllerAdvice
class PhotoSaverExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(ProviderConnectionException::class)
    fun handleProviderConnection(ex: ProviderConnectionException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(PhotoDownloadException::class)
    fun handlePhotoDownload(ex: PhotoDownloadException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(PhotoUploadException::class)
    fun handlePhotoUpload(ex: PhotoUploadException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(PhotoSyncException::class)
    fun handlePhotoSync(ex: PhotoSyncException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    @ExceptionHandler(DataRetrievalException::class)
    fun handleDataRetrieval(ex: DataRetrievalException): ResponseEntity<ErrorDto> =
        ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )

    /** Generic handler for PhotoSaverException. */
    @ExceptionHandler(PhotoSaverException::class)
    fun handlePhotoSaverException(ex: PhotoSaverException): ResponseEntity<ErrorDto> {
        logger.error("Photo saver exception: {}", ex.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(message = ex.message, code = ex.errorCode)
        )
    }
}
