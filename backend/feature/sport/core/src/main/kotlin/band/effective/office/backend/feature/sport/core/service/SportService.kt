package band.effective.office.backend.feature.sport.core.service

import band.effective.office.backend.feature.sport.core.domain.SportProvider
import band.effective.office.backend.feature.sport.core.domain.model.SportUser
import band.effective.office.backend.feature.sport.core.exception.SportUsersRetrievalFailedException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for managing sport users using the configured SportProvider.
 */
@Service
class SportService(
    private val sportProvider: SportProvider
) {

    private val logger = LoggerFactory.getLogger(SportService::class.java)

    /**
     * Retrieves sport users with their time tracking data.
     */
    fun getSportUsers(): List<SportUser> =
        runCatching { sportProvider.getSportUsers() }
            .onFailure { logger.error("Failed to retrieve sport users: ${it.message}", it) }
            .getOrElse { throw SportUsersRetrievalFailedException("Failed to retrieve sport users: ${it.message}") }
}
