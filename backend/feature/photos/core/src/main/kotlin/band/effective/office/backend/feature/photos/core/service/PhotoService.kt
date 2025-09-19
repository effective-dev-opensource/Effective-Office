package band.effective.office.backend.feature.photos.core.service

import band.effective.office.backend.feature.photos.core.domain.PhotoProvider
import band.effective.office.backend.feature.photos.core.domain.model.Photo
import org.slf4j.LoggerFactory
import band.effective.office.backend.feature.photos.core.exception.PhotosCountFailedException
import org.springframework.stereotype.Service

/**
 * Service for managing photos using the configured PhotoProvider.
 */
@Service
class PhotoService(
    private val photoProvider: PhotoProvider
) {

    private val logger = LoggerFactory.getLogger(PhotoService::class.java)

    /**
     * Retrieves photos from the configured provider.
     *
     * @param limit Maximum number of photos to retrieve (optional)
     * @return A list of photos
     */
    fun getPhotos(limit: Int? = null): List<Photo> {
        logger.debug("Retrieving photos, limit: {}", limit)
        return photoProvider.getPhotos(limit)
    }

    /**
     * Gets the total count of photos available.
     *
     * @return The total count of photos
     */
    fun getPhotosCount(): Int {
        logger.debug("Getting photos count")
        return runCatching { photoProvider.getPhotosCount() }
            .getOrElse { throw PhotosCountFailedException("Failed to get photos count: ${it.message}") }
    }

}