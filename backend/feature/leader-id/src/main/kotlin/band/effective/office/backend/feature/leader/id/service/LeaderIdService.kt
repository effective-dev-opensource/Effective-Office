package band.effective.office.backend.feature.leader.id.service

import band.effective.office.backend.feature.leader.id.config.LeaderIdParameters
import band.effective.office.backend.feature.leader.id.domain.exception.EventsNotFoundException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsRetrievalFailedException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsSearchFailedException
import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEventSearchCriteria
import band.effective.office.backend.feature.leader.id.domain.usecase.GetLeaderIdEventsUseCase
import band.effective.office.backend.feature.leader.id.dto.LeaderIdEventsResponseDTO
import band.effective.office.backend.feature.leader.id.infrastructure.mapper.LeaderIdMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Service for retrieving LeaderId event information.
 */
@Service
class LeaderIdService(
    private val getLeaderIdEventsUseCase: GetLeaderIdEventsUseCase,
    private val leaderIdParameters: LeaderIdParameters,
    private val mapper: LeaderIdMapper
) {

    private val logger = LoggerFactory.getLogger(LeaderIdService::class.java)

    /**
     * Retrieves LeaderId events for application.
     * Returns events for the configured period with default parameters.
     *
     * @return [LeaderIdEventsResponseDTO] containing list of event data
     * @throws EventsSearchFailedException if event search fails
     * @throws EventsNotFoundException if no events are found
     * @throws EventsRetrievalFailedException if event retrieval fails
     */
    fun getEventsForTv(): LeaderIdEventsResponseDTO {
        logger.info("Fetching LeaderId events for TV application")
        
        val searchCriteria = createDefaultSearchCriteria()
        val events = getLeaderIdEventsUseCase.execute(searchCriteria)
        val eventDTOs = events.map { mapper.toEventDTO(it) }
        
        logger.info("Successfully fetched ${eventDTOs.size} events")
        return LeaderIdEventsResponseDTO(events = eventDTOs)
    }

    /**
     * Creates default search criteria based on configuration.
     */
    private fun createDefaultSearchCriteria(): LeaderIdEventSearchCriteria {
        val dateFrom = LocalDate.now()
        val dateTo = dateFrom.plusDays(leaderIdParameters.eventsPeriodDays.toLong())

        return LeaderIdEventSearchCriteria(
            dateFrom = dateFrom,
            dateTo = dateTo,
            cityId = leaderIdParameters.defaultCityId,
            placeId = leaderIdParameters.defaultPlaceId,
            paginationSize = leaderIdParameters.defaultPaginationSize
        )
    }
}
