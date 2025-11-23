package band.effective.office.backend.feature.teammates.provider.notion.service

import band.effective.office.backend.feature.teammates.core.domain.TeammateProvider
import band.effective.office.backend.feature.teammates.core.domain.model.Teammate
import band.effective.office.backend.feature.teammates.core.domain.model.TeammateScore
import band.effective.office.backend.feature.teammates.core.exception.TeammatesRetrievalFailedException
import band.effective.office.backend.feature.teammates.provider.notion.client.NotionTeammateClient
import band.effective.office.backend.feature.teammates.provider.notion.mapper.NotionTeammateMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Notion implementation of the TeammateProvider interface.
 * This provider retrieves teammate data from Notion database.
 */
@Component("notionTeammateProvider")
@ConditionalOnProperty(name = ["teammates.provider"], havingValue = "notion")

class NotionTeammateProvider(
    private val notionClient: NotionTeammateClient
) : TeammateProvider {

    private val logger = LoggerFactory.getLogger(NotionTeammateProvider::class.java)

    override fun getTeammates(active: Boolean, employment: List<String>?): List<Teammate> {
        try {
            val pages = notionClient.fetchTeammatePages(active, employment)
            val teammates = pages.map { NotionTeammateMapper.run { it.toTeammate() } }
            logger.info("Retrieved ${teammates.size} teammates (active: $active, employment: ${employment?.joinToString() ?: "all"}) from Notion")
            return teammates
        } catch (e: Exception) {
            logger.error("Failed to retrieve teammates from Notion: ${e.message}", e)
            throw TeammatesRetrievalFailedException("Failed to retrieve teammates from Notion: ${e.message}")
        }
    }

    override fun getTeammateScores(): List<TeammateScore> {
        try {
            val pages = notionClient.fetchSupernovaPages()
            val entries = pages.mapNotNull { NotionTeammateMapper.run { it.toTeammateScore() } }
            val scores = entries
                .groupBy { it.id }
                .map { (userId, parts) ->
                    TeammateScore(id = userId, score = parts.sumOf { it.score })
                }
                .filter { it.score > 0 }
            logger.info("Retrieved ${scores.size} teammate scores from Notion Supernova")
            return scores
        } catch (e: Exception) {
            logger.error("Failed to retrieve teammate scores from Notion: ${e.message}", e)
            throw TeammatesRetrievalFailedException("Failed to retrieve teammate scores from Notion: ${e.message}")
        }
    }
}