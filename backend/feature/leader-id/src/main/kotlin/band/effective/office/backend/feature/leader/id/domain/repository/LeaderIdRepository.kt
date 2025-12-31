package band.effective.office.backend.feature.leader.id.domain.repository

import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEvent
import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEventSearchCriteria

/**
 * Repository interface for LeaderId events.
 *
 */
interface LeaderIdRepository {

    /**
     * Searches for events based on the provided criteria.
     * 
     * @param criteria The search criteria for finding events
     * @return List of event IDs matching the search criteria
     */
    fun searchEventIds(criteria: LeaderIdEventSearchCriteria): List<Int>

    /**
     * Retrieves detailed information for a specific event.
     * 
     * @param eventId The ID of the event to retrieve
     * @return The detailed event information
     */
    fun getEventById(eventId: Int): LeaderIdEvent
}
