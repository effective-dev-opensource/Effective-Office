package band.effective.office.backend.feature.photo.saver.core.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Configuration for enabling Spring Scheduling and defining scheduler constants.
 */
@Configuration
@EnableScheduling
class SchedulerConfig

/**
 * Constants for photo synchronization scheduler.
 */
object PhotoSyncConstants {
    
    /**
     * Sync interval in milliseconds (15 minutes).
     */
    const val SYNC_INTERVAL_MS = 15 * 60 * 1000L
    
    /**
     * Initial delay before first sync in milliseconds (1 minute).
     */
    const val INITIAL_DELAY_MS = 1 * 60 * 1000L
    
    /**
     * Health check interval in milliseconds (1 hour).
     */
    const val HEALTH_CHECK_INTERVAL_MS = 60 * 60 * 1000L
    
    /**
     * Property name for enabling/disabling scheduler.
     */
    const val SCHEDULER_ENABLED_PROPERTY = "photo.saver.scheduler.enabled"
}
