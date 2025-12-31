package band.effective.office.backend.feature.photo.saver.provider.mattermost.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Mattermost provider credentials.
 */
@Configuration
class MattermostCredentialsConfig {

    @Value("\${PHOTO_SAVER_MATTERMOST_BASE_URL}")
    private lateinit var mattermostBaseUrl: String

    @Value("\${PHOTO_SAVER_MATTERMOST_TOKEN}")
    private lateinit var mattermostToken: String

    @Value("\${PHOTO_SAVER_EMOJI_REQUEST_SAVE:star}")
    private lateinit var emojiRequestSave: String

    @Value("\${PHOTO_SAVER_EMOJI_SUCCESS:white_check_mark}")
    private lateinit var emojiSaveSuccess: String

    @Bean("photoSaverMattermostCredentials")
    fun mattermostCredentials(): MattermostCredentials {
        return MattermostCredentials(
            baseUrl = mattermostBaseUrl,
            token = mattermostToken,
            emojiRequestSave = emojiRequestSave,
            emojiSaveSuccess = emojiSaveSuccess
        )
    }
}

/**
 * Data class for Mattermost credentials.
 */
data class MattermostCredentials(
    val baseUrl: String,
    val token: String,
    val emojiRequestSave: String,
    val emojiSaveSuccess: String
)

