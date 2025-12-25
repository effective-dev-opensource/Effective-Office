package band.effective.office.backend.feature.photo.saver.core.scheduler

import band.effective.office.backend.feature.photo.saver.core.config.PhotoSyncConstants
import band.effective.office.backend.feature.photo.saver.core.service.PhotoSyncService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Scheduler for automatic photo synchronization.
 * Periodically triggers photo sync using the configured provider.
 */
@Component
@ConditionalOnProperty(
    name = [PhotoSyncConstants.SCHEDULER_ENABLED_PROPERTY],
    havingValue = "true",
    matchIfMissing = false
)
class PhotoSyncScheduler(
    private val photoSyncService: PhotoSyncService
) {
    private val logger = LoggerFactory.getLogger(PhotoSyncScheduler::class.java)

    /**
     * Runs photo sync at the specified interval.
     * Interval is defined in PhotoSyncConstants.SYNC_INTERVAL_MS
     */
    @Scheduled(
        fixedDelay = PhotoSyncConstants.SYNC_INTERVAL_MS,
        initialDelay = PhotoSyncConstants.INITIAL_DELAY_MS
    )
    fun syncPhotosAutomatically() {
        logger.info("Auto sync: Checking for new photos to sync...")
        
        try {
            photoSyncService.syncPhotos()
            logger.info("Auto sync: Completed successfully")
        } catch (e: Exception) {
            logger.error("Auto sync: Failed - ${e.message}", e)
        }
    }

    /**
     * Health check - logs scheduler status periodically.
     */
    @Scheduled(
        fixedRate = PhotoSyncConstants.HEALTH_CHECK_INTERVAL_MS,
        initialDelay = PhotoSyncConstants.INITIAL_DELAY_MS
    )
    fun healthCheck() {
        logger.debug("Photo sync scheduler is running")
    }
}