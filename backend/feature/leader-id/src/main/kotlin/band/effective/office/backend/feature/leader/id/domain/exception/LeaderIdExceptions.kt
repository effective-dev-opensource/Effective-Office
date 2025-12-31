package band.effective.office.backend.feature.leader.id.domain.exception

/**
 * Base exception class for LeaderId service exceptions.
 * All LeaderId service exceptions should extend this class.
 *
 * @property message Detailed description of the error
 * @property errorCode Error code (not HTTP status code)
 */
open class LeaderIdException(
    override val message: String,
    val errorCode: Int
) : RuntimeException(message)

/**
 * Exception thrown when a specific event is not found.
 */
class EventNotFoundException(
    message: String = "Event not found"
) : LeaderIdException(message, LeaderIdErrorCodes.EVENT_NOT_FOUND)

/**
 * Exception thrown when no events are found for the given criteria.
 */
class EventsNotFoundException(
    message: String = "No events found for the given criteria"
) : LeaderIdException(message, LeaderIdErrorCodes.EVENTS_NOT_FOUND)

/**
 * Exception thrown when the LeaderId API is unavailable.
 */
class LeaderIdApiUnavailableException(
    message: String = "LeaderId API is unavailable"
) : LeaderIdException(message, LeaderIdErrorCodes.API_UNAVAILABLE)

/**
 * Exception thrown when the LeaderId API request times out.
 */
class LeaderIdApiTimeoutException(
    message: String = "LeaderId API request timed out"
) : LeaderIdException(message, LeaderIdErrorCodes.API_TIMEOUT)

/**
 * Exception thrown when authentication with LeaderId API fails.
 */
class LeaderIdApiAuthenticationFailedException(
    message: String = "LeaderId API authentication failed"
) : LeaderIdException(message, LeaderIdErrorCodes.API_AUTHENTICATION_FAILED)

/**
 * Exception thrown when LeaderId API rate limit is exceeded.
 */
class LeaderIdApiRateLimitExceededException(
    message: String = "LeaderId API rate limit exceeded"
) : LeaderIdException(message, LeaderIdErrorCodes.API_RATE_LIMIT_EXCEEDED)

/**
 * Exception thrown when search criteria are invalid.
 */
class InvalidSearchCriteriaException(
    message: String = "Invalid search criteria"
) : LeaderIdException(message, LeaderIdErrorCodes.INVALID_SEARCH_CRITERIA)

/**
 * Exception thrown when event ID is invalid.
 */
class InvalidEventIdException(
    message: String = "Invalid event ID"
) : LeaderIdException(message, LeaderIdErrorCodes.INVALID_EVENT_ID)

/**
 * Exception thrown when data mapping fails.
 */
class DataMappingFailedException(
    message: String = "Data mapping failed"
) : LeaderIdException(message, LeaderIdErrorCodes.DATA_MAPPING_FAILED)

/**
 * Exception thrown when events retrieval fails.
 */
class EventsRetrievalFailedException(
    message: String = "Failed to retrieve events"
) : LeaderIdException(message, LeaderIdErrorCodes.EVENTS_RETRIEVAL_FAILED)

/**
 * Exception thrown when events search fails.
 */
class EventsSearchFailedException(
    message: String = "Failed to search events"
) : LeaderIdException(message, LeaderIdErrorCodes.EVENTS_SEARCH_FAILED)
