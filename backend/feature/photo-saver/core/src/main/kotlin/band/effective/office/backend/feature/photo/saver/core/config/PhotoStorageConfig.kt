package band.effective.office.backend.feature.photo.saver.core.config

import band.effective.office.backend.feature.photo.saver.core.domain.PhotoStorage
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration for selecting the appropriate PhotoStorage implementation for photo-saver feature.
 * Allows switching between different storage backends (Synology, S3, local disk, etc.) via configuration.
 */
@Configuration
class PhotoSaverStorageConfig {

    @Value("\${photo.saver.storage:dummy}")
    private lateinit var storageType: String

    /**
     * Provides the primary PhotoStorage bean based on the configuration.
     * This bean will be injected into PhotoManager.
     *
     * @return The selected PhotoStorage implementation
     * @throws IllegalStateException if the configured storage is not available
     */
    @Bean("photoSaverStorage")
    @Primary
    fun photoSaverStorage(
        synologyPhotoStorage: ObjectProvider<PhotoStorage>,
        dummyPhotoStorage: ObjectProvider<PhotoStorage>
    ): PhotoStorage {
        return when (storageType.lowercase()) {
            "synology" -> {
                synologyPhotoStorage.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Synology PhotoStorage not available. Check configuration and dependencies.") }
            }
            "dummy" -> {
                dummyPhotoStorage.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Dummy PhotoStorage not available. Check configuration and dependencies.") }
            }
            else -> {
                throw IllegalStateException("Unknown photo storage type: $storageType. Supported types: synology, dummy")
            }
        }
    }
}
