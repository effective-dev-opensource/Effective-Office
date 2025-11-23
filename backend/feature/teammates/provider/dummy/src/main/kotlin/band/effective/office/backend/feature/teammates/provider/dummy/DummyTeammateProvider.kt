package band.effective.office.backend.feature.teammates.provider.dummy

import band.effective.office.backend.feature.teammates.core.domain.TeammateProvider
import band.effective.office.backend.feature.teammates.core.domain.model.Teammate
import band.effective.office.backend.feature.teammates.core.domain.model.TeammateScore
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

/**
 * A dummy implementation of the TeammateProvider interface for testing purposes.
 * This implementation returns sample teammates and doesn't interact with any external service.
 */
@Component("dummyTeammateProvider")
@ConditionalOnProperty(name = ["teammates.provider"], havingValue = "dummy", matchIfMissing = true)
class DummyTeammateProvider : TeammateProvider {

    private val logger = LoggerFactory.getLogger(DummyTeammateProvider::class.java)
    private val teammates = ConcurrentHashMap<String, Teammate>()

    init {
        initializeDummyTeammates()
    }

    override fun getTeammates(active: Boolean, employment: List<String>?): List<Teammate> {
        val all = teammates.values.toList()
        return all.filter { teammate ->
            val isActive = !active || teammate.isActive()
            val isCorrectEmployment = employment.isNullOrEmpty() || teammate.employment in employment
            isActive && isCorrectEmployment
        }
    }

    override fun getTeammateScores(): List<TeammateScore> {
        logger.debug("Returning dummy teammate scores")
        return listOf(
            TeammateScore(
                id = "1",
                score = 150
            ),
            TeammateScore(
                id = "2", 
                score = 200
            )
        )
    }

    /**
     * Initializes the provider with some dummy teammates.
     */
    private fun initializeDummyTeammates() {
        val dummyTeammates = listOf(
            Teammate(
                id = "1",
                name = "John Doe",
                positions = listOf("Developer", "Team Lead"),
                employment = "Band",
                startDate = LocalDate.of(2023, 1, 15),
                nextBDay = LocalDate.of(1990, 5, 20),
                duolingo = "johndoe",
                photo = "https://example.com/photos/john.jpg",
                status = "Active"
            ),
            Teammate(
                id = "2",
                name = "Jane Smith",
                positions = listOf("Designer"),
                employment = "Test",
                startDate = LocalDate.of(2023, 3, 10),
                nextBDay = LocalDate.of(1988, 12, 3),
                duolingo = "janesmith",
                photo = "https://example.com/photos/jane.jpg",
                status = "Active"
            ),
        )

        dummyTeammates.forEach { teammate ->
            teammates[teammate.id] = teammate
        }

        logger.info("Initialized {} dummy teammates", teammates.size)
    }
}