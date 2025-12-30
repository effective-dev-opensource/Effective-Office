package band.effective.office.backend.feature.photo.saver.core.domain

/**
 * Interface for photo provider implementations.
 * Providers are responsible for fetching photos from external sources.
 * 
 * This abstraction allows adding new photo sources without changing the core business logic.
 */
interface PhotoProvider {
    
    /**
     * Fetches new photos from the provider source.
     * @return List of photos ready to be stored
     */
    suspend fun fetchNewPhotos(): List<Photo>
    
    /**
     * Returns the name of this provider (e.g., "Mattermost", "Telegram").
     * Used for logging and monitoring.
     * 
     * @return Provider name
     */
    fun getProviderName(): String
    
    /**
     * Checks if the provider is healthy and accessible.
     * Used for monitoring and health checks.
     * 
     * @return true if provider is operational, false otherwise
     */
    fun isHealthy(): Boolean
}
