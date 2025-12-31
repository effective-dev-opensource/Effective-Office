package band.effective.office.backend.feature.leader.id.infrastructure.repository

import band.effective.office.backend.feature.leader.id.api.LeaderIdApi
import band.effective.office.backend.feature.leader.id.domain.exception.EventsSearchFailedException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsRetrievalFailedException
import band.effective.office.backend.feature.leader.id.domain.exception.LeaderIdApiUnavailableException
import band.effective.office.backend.feature.leader.id.domain.exception.LeaderIdApiTimeoutException
import band.effective.office.backend.feature.leader.id.domain.exception.DataMappingFailedException
import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEvent
import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEventSearchCriteria
import band.effective.office.backend.feature.leader.id.domain.repository.LeaderIdRepository
import band.effective.office.backend.feature.leader.id.infrastructure.mapper.LeaderIdMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Implementation of LeaderIdRepository using Spring HTTP Interface.
 * 
 * This implementation handles the actual communication with the LeaderId API,
 * including error handling, logging, and data transformation.
 */
@Repository
class LeaderIdRepositoryImpl(
    private val leaderIdApi: LeaderIdApi,
    private val mapper: LeaderIdMapper
) : LeaderIdRepository {

    private val logger = LoggerFactory.getLogger(LeaderIdRepositoryImpl::class.java)

    override fun searchEventIds(criteria: LeaderIdEventSearchCriteria): List<Int> {
        logger.debug("Searching events with criteria: {}", criteria)
        
        val response = runCatching {
            leaderIdApi.searchEvents(
                paginationSize = criteria.paginationSize,
                dateFrom = criteria.dateFrom.toString(),
                dateTo = criteria.dateTo.toString(),
                cityId = criteria.cityId,
                placeIds = criteria.placeId
            )
        }.getOrElse { throwable ->
            when (throwable) {
                is WebClientResponseException -> {
                    when (throwable.statusCode.value()) {
                        401 -> throw LeaderIdApiUnavailableException("LeaderId API authentication failed: ${throwable.message}")
                        429 -> throw LeaderIdApiUnavailableException("LeaderId API rate limit exceeded: ${throwable.message}")
                        500, 502, 503, 504 -> throw LeaderIdApiUnavailableException("LeaderId API server error: ${throwable.message}")
                        else -> throw EventsSearchFailedException("LeaderId API request failed with status ${throwable.statusCode}: ${throwable.message}")
                    }
                }
                is java.util.concurrent.TimeoutException -> throw LeaderIdApiTimeoutException("LeaderId API request timed out: ${throwable.message}")
                else -> throw EventsSearchFailedException("Failed to search events: ${throwable.message}")
            }
        }
        
        val eventIds = response?.data?.items?.map { it.id } ?: emptyList()
        logger.debug("Found {} event IDs", eventIds.size)
        return eventIds
    }

    override fun getEventById(eventId: Int): LeaderIdEvent {
        logger.debug("Fetching event details for ID: {}", eventId)
        
        val response = runCatching {
            leaderIdApi.getEventInfo(eventId = eventId)
        }.getOrElse { throwable ->
            when (throwable) {
                is WebClientResponseException -> {
                    when (throwable.statusCode.value()) {
                        404 -> throw EventsRetrievalFailedException("Event with ID $eventId not found")
                        401 -> throw LeaderIdApiUnavailableException("LeaderId API authentication failed: ${throwable.message}")
                        429 -> throw LeaderIdApiUnavailableException("LeaderId API rate limit exceeded: ${throwable.message}")
                        500, 502, 503, 504 -> throw LeaderIdApiUnavailableException("LeaderId API server error: ${throwable.message}")
                        else -> throw EventsRetrievalFailedException("LeaderId API request failed with status ${throwable.statusCode}: ${throwable.message}")
                    }
                }
                is java.util.concurrent.TimeoutException -> throw LeaderIdApiTimeoutException("LeaderId API request timed out: ${throwable.message}")
                else -> throw EventsRetrievalFailedException("Failed to retrieve event with ID $eventId: ${throwable.message}")
            }
        }
        
        return runCatching {
            mapper.toDomainEvent(response)
        }.getOrElse { throwable ->
            throw DataMappingFailedException("Failed to map event data for ID $eventId: ${throwable.message}")
        }
    }
}
