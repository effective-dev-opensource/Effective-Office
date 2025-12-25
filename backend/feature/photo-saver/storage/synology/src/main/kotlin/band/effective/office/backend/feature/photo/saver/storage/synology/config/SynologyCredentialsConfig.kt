package band.effective.office.backend.feature.photo.saver.storage.synology.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Synology storage credentials.
 */
@Configuration("photoSaverSynologyCredentialsConfig")
class SynologyCredentialsConfig {

    @Value("\${PHOTO_SAVER_SYNOLOGY_BASE_URL}")
    private lateinit var synologyBaseUrl: String

    @Value("\${PHOTO_SAVER_SYNOLOGY_USERNAME}")
    private lateinit var synologyUsername: String

    @Value("\${PHOTO_SAVER_SYNOLOGY_PASSWORD}")
    private lateinit var synologyPassword: String

    @Value("\${PHOTO_SAVER_SYNOLOGY_ALBUM_NAME}")
    private lateinit var albumName: String

    @Bean("photoSaverSynologyCredentials")
    fun synologyCredentials(): SynologyCredentials {
        return SynologyCredentials(
            baseUrl = synologyBaseUrl,
            username = synologyUsername,
            password = synologyPassword,
            albumName = albumName
        )
    }
}
