package band.effective.office.backend.feature.photo.saver.core.exception

/**
 * Base exception class for photo saver feature exceptions.
 * All photo saver feature exceptions should extend this class.
 *
 * @property message Detailed description of the error
 * @property errorCode Error code (not HTTP status code)
 */
open class PhotoSaverException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)

/**
 * Exception thrown when provider connection fails.
 */
class ProviderConnectionException(
    message: String = "Failed to connect to provider"
) : PhotoSaverException(message, PhotoSaverErrorCodes.CONNECTION_FAILED)

/**
 * Exception thrown when photo download fails.
 */
class PhotoDownloadException(
    message: String = "Failed to download photo"
) : PhotoSaverException(message, PhotoSaverErrorCodes.DOWNLOAD_FAILED)

/**
 * Exception thrown when photo upload fails.
 */
class PhotoUploadException(
    message: String = "Failed to upload photo"
) : PhotoSaverException(message, PhotoSaverErrorCodes.UPLOAD_FAILED)

/**
 * Exception thrown when photo synchronization fails.
 */
class PhotoSyncException(
    message: String = "Photo synchronization failed"
) : PhotoSaverException(message, PhotoSaverErrorCodes.SYNC_FAILED)

/**
 * Exception thrown when data retrieval from provider fails.
 */
class DataRetrievalException(
    message: String = "Failed to retrieve data from provider"
) : PhotoSaverException(message, PhotoSaverErrorCodes.DATA_RETRIEVAL_FAILED)
