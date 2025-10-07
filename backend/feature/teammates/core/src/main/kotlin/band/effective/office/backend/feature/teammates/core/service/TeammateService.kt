package band.effective.office.backend.feature.teammates.core.service

import band.effective.office.backend.feature.teammates.core.domain.TeammateProvider
import band.effective.office.backend.feature.teammates.core.domain.model.Teammate
import band.effective.office.backend.feature.teammates.core.domain.model.TeammateScore
import band.effective.office.backend.feature.teammates.core.exception.TeammatesRetrievalFailedException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for managing teammates using the configured TeammateProvider.
 */
@Service
class TeammateService(
    private val teammateProvider: TeammateProvider
) {

    private val logger = LoggerFactory.getLogger(TeammateService::class.java)

    /**
     * Retrieves teammates with optional active filter applied.
     */
    fun getTeammates(active: Boolean): List<Teammate> =
        runCatching { teammateProvider.getTeammates(active) }
            .onFailure { logger.error("Failed to retrieve teammates: ${it.message}", it) }
            .getOrElse { throw TeammatesRetrievalFailedException("Failed to retrieve teammates: ${it.message}") }

    /**
     * Retrieves teammate scores from the Notion database.
     */
    fun getTeammateScores(): List<TeammateScore> =
        runCatching { teammateProvider.getTeammateScores() }
            .onFailure { logger.error("Failed to retrieve teammate scores: ${it.message}", it) }
            .getOrElse { throw TeammatesRetrievalFailedException("Failed to retrieve teammate scores: ${it.message}") }
}