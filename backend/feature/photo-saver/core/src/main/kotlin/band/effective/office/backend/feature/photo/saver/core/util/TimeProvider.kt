package band.effective.office.backend.feature.photo.saver.core.util

/**
 * Provider for current time operations.
 * Abstraction over System.currentTimeMillis() for testability.
 */
interface TimeProvider {
    
    /**
     * Returns current time in milliseconds since epoch.
     */
    fun currentTimeMillis(): Long
    
    /**
     * Returns timestamp for yesterday (24 hours ago).
     */
    fun yesterdayTimestamp(): Long = currentTimeMillis() - MILLISECONDS_IN_DAY
    
    companion object {
        const val MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000L
    }
}

/**
 * Default implementation using System.currentTimeMillis().
 */
class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
