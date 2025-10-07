package band.effective.office.backend.feature.teammates.provider.notion.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import notion.api.v1.NotionClient

@Configuration
class NotionTeammateConfig {

    @Value("\${NOTION_TOKEN}")
    private lateinit var notionToken: String

    @Value("\${NOTION_TEAMMATES_DB_ID}")
    private lateinit var notionTeammatesDatabaseId: String

    @Value("\${NOTION_SUPERNOVA_DB_ID}")
    private lateinit var notionSupernovaDbId: String

    @Bean
    fun notionCredentials(): NotionCredentials {
        return NotionCredentials(
            token = notionToken,
            teammatesDatabaseId = notionTeammatesDatabaseId,
            supernovaDatabaseId = notionSupernovaDbId
        )
    }

    @Bean
    fun notionClient(): NotionClient = NotionClient(token = notionToken)
}

/**
 * Data class for Notion credentials.
 */
data class NotionCredentials(
    val token: String,
    val teammatesDatabaseId: String,
    val supernovaDatabaseId: String
)


