package band.effective.office.backend.feature.photo.saver.provider.dummy

import band.effective.office.backend.feature.photo.saver.core.domain.Photo
import band.effective.office.backend.feature.photo.saver.core.domain.PhotoMetadata
import band.effective.office.backend.feature.photo.saver.core.domain.PhotoProvider
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * A dummy implementation of PhotoProvider for testing purposes.
 * Returns sample photos without connecting to any external service.
 */
@Component("dummyPhotoProvider")
@ConditionalOnProperty(name = ["photo.saver.provider"], havingValue = "dummy", matchIfMissing = true)
class DummyPhotoProvider : PhotoProvider {

    private val logger = LoggerFactory.getLogger(DummyPhotoProvider::class.java)

    override suspend fun fetchNewPhotos(): List<Photo> {
        logger.info("DummyPhotoProvider: Fetching new photos (returning dummy data)")
        
        // Return a single dummy 1x1 pixel PNG image
        val dummyImageBytes = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, 0xC4.toByte()
        )
        
        return listOf(
            Photo(
                fileBytes = dummyImageBytes,
                fileName = "dummy-photo-1.png",
                mimeType = "image/png",
                metadata = PhotoMetadata(
                    providerName = getProviderName(),
                    originalId = "dummy-photo-1",
                    createdAt = Instant.now(),
                    authorId = "dummy-user-1",
                    authorName = "Dummy User"
                )
            )
        )
    }

    override fun getProviderName(): String = "Dummy"

    override fun isHealthy(): Boolean {
        logger.debug("DummyPhotoProvider: Health check - always healthy")
        return true
    }
}
