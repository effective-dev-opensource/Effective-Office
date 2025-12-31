package band.effective.office.backend.feature.photos.provider.dummy

import band.effective.office.backend.feature.photos.core.domain.PhotoProvider
import band.effective.office.backend.feature.photos.core.domain.model.Photo
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * A dummy implementation of the PhotoProvider interface for testing purposes.
 * This implementation returns sample photos and doesn't interact with any external photo service.
 */
@Component("dummyPhotoProvider")
@ConditionalOnProperty(name = ["photos.provider"], havingValue = "dummy", matchIfMissing = true)
class DummyPhotoProvider : PhotoProvider {

    private val logger = LoggerFactory.getLogger(DummyPhotoProvider::class.java)
    private val photos = ConcurrentHashMap<String, Photo>()

    init {
        // Initialize with some dummy photos
        initializeDummyPhotos()
    }

    override fun getPhotos(limit: Int?): List<Photo> {
        logger.debug("Retrieving dummy photos, limit: {}", limit)

        val allPhotos = photos.values.toList()
        return if (limit != null) allPhotos.take(limit.coerceAtLeast(0)) else allPhotos
    }

    override fun getPhotosCount(): Int {
        logger.debug("Getting dummy photos count")
        
        val allPhotos = photos.values.toList()
        return allPhotos.size
    }

    // Availability check removed with interface simplification

    /**
     * Initializes the provider with some dummy photos.
     */
    private fun initializeDummyPhotos() {
        val dummyPhotos = listOf(
            Photo(
                id = "dummy-photo-1",
                thumbnailUrl = "https://picsum.photos/300/200?random=1"
            ),
            Photo(
                id = "dummy-photo-2",
                thumbnailUrl = "https://picsum.photos/300/200?random=2"
            ),
            Photo(
                id = "dummy-photo-3",
                thumbnailUrl = "https://picsum.photos/300/200?random=3"
            ),
            Photo(
                id = "dummy-photo-4",
                thumbnailUrl = "https://picsum.photos/300/200?random=4"
            ),
            Photo(
                id = "dummy-photo-5",
                thumbnailUrl = "https://picsum.photos/300/200?random=5"
            )
        )

        dummyPhotos.forEach { photo ->
            photos[photo.id] = photo
        }

        logger.info("Initialized dummy photo provider with ${dummyPhotos.size} sample photos")
    }
}