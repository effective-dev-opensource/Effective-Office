package band.effective.office.backend.feature.photo.saver.core.service

import band.effective.office.backend.feature.photo.saver.core.domain.PhotoProvider
import band.effective.office.backend.feature.photo.saver.core.domain.PhotoStorage
import band.effective.office.backend.feature.photo.saver.core.exception.DataRetrievalException
import band.effective.office.backend.feature.photo.saver.core.exception.PhotoSyncException
import band.effective.office.backend.feature.photo.saver.core.exception.PhotoUploadException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Central photo management service.
 * Orchestrates photo synchronization between provider and storage.
 * 
 * This is the main business logic component where all operations converge:
 * - Fetches photos from the configured provider
 * - Uploads photos to the configured storage
 * - Handles errors and logging
 */
@Service
class PhotoManager(
    private val provider: PhotoProvider,
    private val storage: PhotoStorage
) {
    private val logger = LoggerFactory.getLogger(PhotoManager::class.java)

    /**
     * Synchronizes photos from provider to storage.
     * Fetches photos from the configured provider and uploads them to storage.
     * 
     * @throws PhotoSyncException if synchronization fails critically
     */
    suspend fun syncPhotos() = coroutineScope {
        val providerName = provider.getProviderName()
        logger.info("Starting photo synchronization ")
        logger.info("Provider: $providerName")
        logger.info("Storage health check...")
        
        // Check storage health before starting
        if (!storage.isHealthy()) {
            val errorMsg = "Storage is not healthy, aborting synchronization"
            logger.error(errorMsg)
            throw PhotoSyncException(errorMsg)
        }
        logger.info("Storage is healthy")

        // Fetch photos from provider
        logger.info("Fetching photos from provider: $providerName...")
        val allPhotos = try {
            val photos = provider.fetchNewPhotos()
            logger.info("Successfully fetched ${photos.size} photo(s) from $providerName")
            if (photos.isEmpty()) {
                logger.info("No new photos to sync")
            }
            photos
        } catch (e: Exception) {
            logger.error("Failed to fetch photos from provider $providerName", e)
            throw DataRetrievalException("Failed to fetch photos from $providerName: ${e.message}")
        }

        if (allPhotos.isEmpty()) {
            logger.info("Photo synchronization completed: nothing to sync")
            return@coroutineScope
        }

        // Upload all photos to storage sequentially
        logger.info("Uploading ${allPhotos.size} photo(s) to storage...")
        var successCount = 0

        allPhotos.forEach { photo ->
            try {
                logger.debug("Uploading: ${photo.fileName} (${photo.fileBytes.size} bytes, ${photo.mimeType})")
                val result = storage.uploadPhoto(
                    fileBytes = photo.fileBytes,
                    fileName = photo.fileName,
                    mimeType = photo.mimeType
                )
                
                if (result.isSuccess) {
                    successCount++
                    logger.debug("Successfully uploaded: ${photo.fileName}")
                } else {
                    val error = result.exceptionOrNull()
                    logger.warn("Failed to upload ${photo.fileName}: ${error?.message}", error)
                }
            } catch (e: Exception) {
                logger.error("Exception while uploading ${photo.fileName}", e)
            }
        }

        logger.info("Photo synchronization completed: $successCount of ${allPhotos.size} uploaded successfully")
    }

    /**
     * Gets health status of provider and storage.
     * @return Map of component name to health status
     */
    fun getHealthStatus(): Map<String, Boolean> {
        logger.debug("Checking health status of provider and storage")
        val status = mutableMapOf<String, Boolean>()
        
        val providerName = provider.getProviderName()
        status["provider:$providerName"] = try {
            val isHealthy = provider.isHealthy()
            if (isHealthy) {
                logger.debug("Provider $providerName is healthy")
            } else {
                logger.warn("Provider $providerName reported unhealthy status")
            }
            isHealthy
        } catch (e: Exception) {
            logger.error("Health check failed for provider $providerName", e)
            false
        }
        
        status["storage"] = try {
            val isHealthy = storage.isHealthy()
            if (isHealthy) {
                logger.debug("Storage is healthy")
            } else {
                logger.warn("Storage reported unhealthy status")
            }
            isHealthy
        } catch (e: Exception) {
            logger.error("Health check failed for storage", e)
            false
        }
        
        logger.debug("Health status: $status")
        return status
    }
}
