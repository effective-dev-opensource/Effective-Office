package band.effective.office.backend.feature.photo.saver.core.service

import band.effective.office.backend.feature.photo.saver.core.exception.PhotoSyncException
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for synchronizing photos using the configured providers.
 * Delegates to PhotoManager which orchestrates all providers and storage.
 */
@Service
class PhotoSyncService(
    private val photoManager: PhotoManager
) {

    private val logger = LoggerFactory.getLogger(PhotoSyncService::class.java)

    /**
     * Synchronizes photos from provider to storage.
     * Uses PhotoManager to coordinate the entire workflow.
     */
    fun syncPhotos() {
        try {
            logger.info("Starting photo synchronization via PhotoManager")
            
            runBlocking {
                photoManager.syncPhotos()
            }
            
            logger.info("Photo synchronization completed")
        } catch (e: Exception) {
            logger.error("Photo synchronization failed: ${e.message}", e)
            throw PhotoSyncException("Photo synchronization failed: ${e.message}")
        }
    }
}