package band.effective.office.backend.feature.photos.provider.synology.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SynologyConfig {

    @Value("\${SYNOLOGY_IP}")
    private lateinit var synologyIp: String

    @Value("\${SYNOLOGY_LOGIN}")
    private lateinit var synologyLogin: String

    @Value("\${SYNOLOGY_PASSWORD}")
    private lateinit var synologyPassword: String

    @Value("\${SYNOLOGY_ALBUM_NAME:}")
    private lateinit var synologyAlbumName: String

    @Bean
    fun synologyCredentials(): SynologyCredentials {
        return SynologyCredentials(
            url = synologyIp,
            login = synologyLogin,
            password = synologyPassword,
            albumName = synologyAlbumName
        )
    }
}

/**
 * Data class for Synology credentials.
 */
data class SynologyCredentials(
    val url: String,
    val login: String,
    val password: String,
    val albumName: String
)