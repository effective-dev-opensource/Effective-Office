package band.effective.office.backend.feature.photo.saver.storage.dummy

import band.effective.office.backend.feature.photo.saver.core.domain.PhotoStorage
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Dummy implementation of PhotoStorage for testing and development.
 * Logs operations without actual storage interaction.
 */
@Component("dummyPhotoStorage")
@ConditionalOnProperty(name = ["photo.saver.storage"], havingValue = "dummy", matchIfMissing = true)
class DummyPhotoStorage : PhotoStorage {
    
    private val logger = LoggerFactory.getLogger(DummyPhotoStorage::class.java)

    override fun uploadPhoto(fileBytes: ByteArray, fileName: String, mimeType: String): Result<Boolean> {
        logger.info("DUMMY: Upload photo - fileName=$fileName, size=${fileBytes.size} bytes, mimeType=$mimeType")
        return Result.success(true)
    }

    override fun createAlbum(albumName: String): Result<Int> {
        val dummyAlbumId = albumName.hashCode()
        logger.info("DUMMY: Create album - name=$albumName, returning dummy ID=$dummyAlbumId")
        return Result.success(dummyAlbumId)
    }

    override fun isHealthy(): Boolean {
        logger.debug("DUMMY: Health check - always healthy")
        return true
    }
}
