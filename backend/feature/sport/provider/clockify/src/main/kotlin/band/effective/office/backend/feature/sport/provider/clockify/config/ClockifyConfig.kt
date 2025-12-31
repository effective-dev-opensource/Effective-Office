package band.effective.office.backend.feature.sport.provider.clockify.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Clockify API integration.
 * Provides ClockifyCredentials bean with values from environment variables.
 */
@Configuration
class ClockifyConfig {

    @Value("\${CLOCKIFY_API_KEY}")
    private lateinit var clockifyApiKey: String

    @Value("\${CLOCKIFY_WORKSPACE_ID}")
    private lateinit var clockifyWorkspaceId: String

    @Value("\${CLOCKIFY_PROJECT_ID}")
    private lateinit var clockifyProjectId: String

    @Bean
    fun clockifyCredentials(): ClockifyCredentials {
        return ClockifyCredentials(
            apiKey = clockifyApiKey,
            workspaceId = clockifyWorkspaceId,
            projectId = clockifyProjectId
        )
    }
}

/**
 * Data class for Clockify credentials.
 */
data class ClockifyCredentials(
    val apiKey: String,
    val workspaceId: String,
    val projectId: String
)