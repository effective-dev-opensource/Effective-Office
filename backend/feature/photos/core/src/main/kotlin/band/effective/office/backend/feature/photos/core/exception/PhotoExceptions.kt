package band.effective.office.backend.feature.photos.core.exception

/**
 * Base exception class for photos feature exceptions.
 * All photos feature exceptions should extend this class.
 *
 * @property message Detailed description of the error
 * @property errorCode Error code (not HTTP status code)
 */
open class PhotosException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)

/**
 * Exception thrown when photos provider is misconfigured or unavailable.
 */
class PhotoProviderUnavailableException(
    message: String = "Photo provider is unavailable"
) : PhotosException(message, PhotosErrorCodes.PROVIDER_UNAVAILABLE)

/**
 * Exception thrown when photos retrieval fails for any reason.
 */
class PhotosRetrievalFailedException(
    message: String = "Failed to retrieve photos"
) : PhotosException(message, PhotosErrorCodes.RETRIEVAL_FAILED)

/**
 * Exception thrown when photos count retrieval fails.
 */
class PhotosCountFailedException(
    message: String = "Failed to retrieve photos count"
) : PhotosException(message, PhotosErrorCodes.COUNT_FAILED)

/**
 * Exception thrown when input parameters are invalid.
 */
class InvalidPhotoRequestException(
    message: String = "Invalid photo request"
) : PhotosException(message, PhotosErrorCodes.INVALID_REQUEST)