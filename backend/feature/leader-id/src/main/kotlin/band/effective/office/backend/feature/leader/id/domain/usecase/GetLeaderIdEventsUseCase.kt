package band.effective.office.backend.feature.leader.id.domain.usecase

import band.effective.office.backend.feature.leader.id.domain.exception.EventsNotFoundException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsRetrievalFailedException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsSearchFailedException
import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEvent
import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEventSearchCriteria
import band.effective.office.backend.feature.leader.id.domain.repository.LeaderIdRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Use case for retrieving LeaderId events.
 * 
 * This use case encapsulates the business logic for fetching events from the LeaderId platform.
 */
@Component
class GetLeaderIdEventsUseCase(
    private val leaderIdRepository: LeaderIdRepository
) {
    private val logger = LoggerFactory.getLogger(GetLeaderIdEventsUseCase::class.java)

    /**
     * Retrieves events based on the provided search criteria.
     * 
     * @param criteria The search criteria for finding events
     * @return List of events matching the criteria
     * @throws EventsSearchFailedException if event search fails
     * @throws EventsNotFoundException if no events are found
     * @throws EventsRetrievalFailedException if event retrieval fails
     */
    fun execute(criteria: LeaderIdEventSearchCriteria): List<LeaderIdEvent> {
        val eventIds = try {
            leaderIdRepository.searchEventIds(criteria)
        } catch (e: Exception) {
            throw EventsSearchFailedException("Failed to search events: ${e.message}")
        }
        
        if (eventIds.isEmpty()) {
            throw EventsNotFoundException("No events found for the given search criteria")
        }
        
        val events = eventIds.mapNotNull { eventId ->
            try {
                leaderIdRepository.getEventById(eventId)
            } catch (e: Exception) {
                logger.warn("Failed to retrieve event with ID {}: {}", eventId, e.message)
                null
            }
        }
        
        if (events.isEmpty()) {
            throw EventsRetrievalFailedException("Failed to retrieve any events from the found event IDs")
        }
        
        logger.info("Successfully retrieved {} events", events.size)
        return events
    }
}
