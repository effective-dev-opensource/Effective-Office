package band.effective.office.backend.feature.photo.saver.core.config

import band.effective.office.backend.feature.photo.saver.core.domain.PhotoProvider
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration for selecting the appropriate PhotoProvider implementation for photo-saver feature.
 * Allows switching between different photo sources (Mattermost, Telegram, etc.) via configuration.
 */
@Configuration
class PhotoSaverProviderConfig {

    @Value("\${photo.saver.provider:dummy}")
    private lateinit var providerType: String

    /**
     * Provides the primary PhotoProvider bean based on the configuration.
     * This bean will be injected into PhotoManager.
     *
     * @return The selected PhotoProvider implementation
     * @throws IllegalStateException if the configured provider is not available
     */
    @Bean("photoSaverProvider")
    @Primary
    fun photoSaverProvider(
        mattermostPhotoProvider: ObjectProvider<PhotoProvider>,
        dummyPhotoProvider: ObjectProvider<PhotoProvider>
    ): PhotoProvider {
        return when (providerType.lowercase()) {
            "mattermost" -> {
                mattermostPhotoProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Mattermost PhotoProvider not available. Check configuration and dependencies.") }
            }
            "dummy" -> {
                dummyPhotoProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Dummy PhotoProvider not available. Check configuration and dependencies.") }
            }
            else -> {
                throw IllegalStateException("Unknown photo provider type: $providerType. Supported types: mattermost, dummy")
            }
        }
    }
}
