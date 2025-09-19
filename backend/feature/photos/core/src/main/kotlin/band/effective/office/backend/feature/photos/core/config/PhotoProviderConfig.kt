package band.effective.office.backend.feature.photos.core.config

import band.effective.office.backend.feature.photos.core.domain.PhotoProvider
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration for selecting the appropriate PhotoProvider based on the configuration.
 */
@Configuration
class PhotoProviderConfig {

    @Value("\${photos.provider:dummy}")
    private lateinit var providerType: String

    /**
     * Provides the primary PhotoProvider bean based on the configuration.
     * This bean will be injected into the PhotoService.
     *
     * @param synologyPhotoProvider The Synology photo provider (if available)
     * @param dummyPhotoProvider The dummy photo provider (if available)
     * @return The selected PhotoProvider
     */
    @Bean
    @Primary
    fun photoProvider(
        synologyPhotoProvider: ObjectProvider<PhotoProvider>,
        dummyPhotoProvider: ObjectProvider<PhotoProvider>
    ): PhotoProvider {
        return when (providerType.lowercase()) {
            "synology" -> {
                synologyPhotoProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Synology PhotoProvider not available. Check configuration and dependencies.") }
            }
            "dummy" -> {
                dummyPhotoProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Dummy PhotoProvider not available. Check configuration and dependencies.") }
            }
            else -> {
                throw IllegalStateException("Unknown photo provider type: $providerType. Supported types: synology, dummy")
            }
        }
    }
}
