package band.effective.office.backend.feature.photos.core.domain

import band.effective.office.backend.feature.photos.core.domain.model.Photo

/**
 * Interface for photo providers.
 * This interface defines the operations that photo providers must implement.
 */
interface PhotoProvider {
    /**
     * Retrieves photos from the provider.
     *
     * @param limit Maximum number of photos to retrieve (optional)
     * @return A list of photos from the provider
     */
    fun getPhotos(limit: Int? = null): List<Photo>

    /**
     * Gets the total count of photos available from this provider.
     *
     * @return The total count of photos
     */
    fun getPhotosCount(): Int
}