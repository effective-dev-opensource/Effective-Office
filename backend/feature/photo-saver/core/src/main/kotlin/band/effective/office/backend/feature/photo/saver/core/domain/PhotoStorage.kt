package band.effective.office.backend.feature.photo.saver.core.domain

/**
 * Interface for photo storage implementations.
 * Defines operations for storing photos in external storage systems (e.g., Synology NAS).
 * 
 * This abstraction allows switching between different storage backends without changing business logic.
 */
interface PhotoStorage {
    
    /**
     * Uploads a photo to the storage system.
     * @return Result<Boolean> - Success with true if uploaded, Failure with exception if error occurred
     */
    fun uploadPhoto(fileBytes: ByteArray, fileName: String, mimeType: String): Result<Boolean>
    
    /**
     * Creates a new album in the storage system.
     * Note: Not all storage implementations support this operation.
     * @return Result<Int> - Success with album ID if created, Failure if not supported or error occurred
     */
    fun createAlbum(albumName: String): Result<Int>
    
    /**
     * Checks if the storage system is healthy and accessible.
     *
     * @return true if storage is operational, false otherwise
     */
    fun isHealthy(): Boolean
}
