package band.effective.office.backend.feature.sport.provider.dummy

import band.effective.office.backend.feature.sport.core.domain.SportProvider
import band.effective.office.backend.feature.sport.core.domain.model.SportUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * A dummy implementation of the SportProvider interface for testing purposes.
 * This implementation returns sample sport users and doesn't interact with any external service.
 */
@Component("dummySportProvider")
@ConditionalOnProperty(name = ["sport.provider"], havingValue = "dummy", matchIfMissing = true)
class DummySportProvider : SportProvider {

    private val logger = LoggerFactory.getLogger(DummySportProvider::class.java)

    override fun getSportUsers(): List<SportUser> {
        logger.info("Retrieving dummy sport users")
        
        return listOf(
            SportUser(
                name = "John Doe",
                email = "john.doe@example.com",
                totalSeconds = 7200
            ),
            SportUser(
                name = "Jane Smith",
                email = "jane.smith@example.com",
                totalSeconds = 10800
            ),
            SportUser(
                name = "Bob Johnson",
                email = "bob.johnson@example.com",
                totalSeconds = 5400
            ),
            SportUser(
                name = "Alice Brown",
                email = "alice.brown@example.com",
                totalSeconds = 9000
            )
        )
    }
}