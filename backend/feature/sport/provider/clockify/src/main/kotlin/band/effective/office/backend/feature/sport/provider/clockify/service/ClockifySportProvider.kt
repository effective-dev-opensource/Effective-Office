package band.effective.office.backend.feature.sport.provider.clockify.service

import band.effective.office.backend.feature.sport.core.domain.SportProvider
import band.effective.office.backend.feature.sport.core.domain.model.SportUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Clockify implementation of the SportProvider interface.
 * This provider retrieves sport time tracking data from Clockify API.
 */
@Component("clockifySportProvider")
@ConditionalOnProperty(name = ["sport.provider"], havingValue = "clockify")
class ClockifySportProvider(
    private val clockifySportService: ClockifySportService
) : SportProvider {

    private val logger = LoggerFactory.getLogger(ClockifySportProvider::class.java)

    override fun getSportUsers(): List<SportUser> {
        logger.debug("Retrieving sport users from Clockify")
        return clockifySportService.getSportUsers()
    }
}