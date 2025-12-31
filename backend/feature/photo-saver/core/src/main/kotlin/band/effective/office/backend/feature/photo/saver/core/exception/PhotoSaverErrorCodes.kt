package band.effective.office.backend.feature.photo.saver.core.exception

/**
 * Error codes for the photo saver feature.
 * These constants define the error codes that can be returned by the photo saver feature.
 */
object PhotoSaverErrorCodes {
    // Provider / infrastructure errors (4xx)
    const val CONNECTION_FAILED = 401
    const val DATA_RETRIEVAL_FAILED = 402

    // Download / Upload errors (5xx)
    const val DOWNLOAD_FAILED = 501
    const val UPLOAD_FAILED = 502
    const val SYNC_FAILED = 503
}
