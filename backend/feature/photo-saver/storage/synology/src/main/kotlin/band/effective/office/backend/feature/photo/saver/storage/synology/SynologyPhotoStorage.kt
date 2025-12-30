package band.effective.office.backend.feature.photo.saver.storage.synology

import band.effective.office.backend.feature.photo.saver.core.domain.PhotoStorage
import band.effective.office.backend.feature.photo.saver.storage.synology.service.PhotoSaverSessionService
import band.effective.office.backend.feature.photo.saver.storage.synology.service.SynologyPhotoUploadService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Synology NAS implementation of PhotoStorage.
 * Provides photo upload functionality to Synology Photos application.
 */
@Component("synologyPhotoStorage")
@ConditionalOnProperty(name = ["photo.saver.storage"], havingValue = "synology")
class SynologyPhotoStorage(
    private val uploadService: SynologyPhotoUploadService,
    private val sessionService: PhotoSaverSessionService
) : PhotoStorage {
    
    private val logger = LoggerFactory.getLogger(SynologyPhotoStorage::class.java)

    override fun uploadPhoto(fileBytes: ByteArray, fileName: String, mimeType: String): Result<Boolean> {
        return runCatching {
            logger.info("Uploading photo to Synology: $fileName")
            uploadService.uploadPhoto(fileBytes, fileName, mimeType)
        }.onFailure { error ->
            logger.error("Failed to upload photo $fileName to Synology", error)
        }
    }

    override fun createAlbum(albumName: String): Result<Int> {
        return Result.failure(
            UnsupportedOperationException("Direct album creation not supported. Albums are managed automatically via configuration.")
        )
    }

    override fun isHealthy(): Boolean {
        return runCatching {
            // Check if we can get a valid session
            val cookie = sessionService.getValidCookie()
            cookie.isNotBlank()
        }.getOrElse { 
            logger.warn("Health check failed", it)
            false 
        }
    }
}
