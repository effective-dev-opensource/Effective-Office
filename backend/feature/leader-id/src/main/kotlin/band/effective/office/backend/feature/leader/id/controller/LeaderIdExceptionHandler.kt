package band.effective.office.backend.feature.leader.id.controller

import band.effective.office.backend.core.data.ErrorDto
import band.effective.office.backend.feature.leader.id.domain.exception.EventNotFoundException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsNotFoundException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsRetrievalFailedException
import band.effective.office.backend.feature.leader.id.domain.exception.EventsSearchFailedException
import band.effective.office.backend.feature.leader.id.domain.exception.InvalidEventIdException
import band.effective.office.backend.feature.leader.id.domain.exception.InvalidSearchCriteriaException
import band.effective.office.backend.feature.leader.id.domain.exception.LeaderIdApiAuthenticationFailedException
import band.effective.office.backend.feature.leader.id.domain.exception.LeaderIdApiRateLimitExceededException
import band.effective.office.backend.feature.leader.id.domain.exception.LeaderIdApiTimeoutException
import band.effective.office.backend.feature.leader.id.domain.exception.LeaderIdApiUnavailableException
import band.effective.office.backend.feature.leader.id.domain.exception.LeaderIdException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Exception handler for LeaderId feature exceptions.
 * Provides centralized error handling for all LeaderId-related exceptions.
 */
@RestControllerAdvice
class LeaderIdExceptionHandler {
    private val logger = LoggerFactory.getLogger(LeaderIdExceptionHandler::class.java)

    /**
     * Exception handler for EventNotFoundException.
     */
    @ExceptionHandler(EventNotFoundException::class)
    fun handleEventNotFoundException(ex: EventNotFoundException): ResponseEntity<ErrorDto> {
        logger.warn("Event not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for EventsNotFoundException.
     */
    @ExceptionHandler(EventsNotFoundException::class)
    fun handleEventsNotFoundException(ex: EventsNotFoundException): ResponseEntity<ErrorDto> {
        logger.warn("No events found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for LeaderIdApiUnavailableException.
     */
    @ExceptionHandler(LeaderIdApiUnavailableException::class)
    fun handleApiUnavailableException(ex: LeaderIdApiUnavailableException): ResponseEntity<ErrorDto> {
        logger.error("LeaderId API unavailable: {}", ex.message)
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for LeaderIdApiTimeoutException.
     */
    @ExceptionHandler(LeaderIdApiTimeoutException::class)
    fun handleApiTimeoutException(ex: LeaderIdApiTimeoutException): ResponseEntity<ErrorDto> {
        logger.error("LeaderId API timeout: {}", ex.message)
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for LeaderIdApiAuthenticationFailedException.
     */
    @ExceptionHandler(LeaderIdApiAuthenticationFailedException::class)
    fun handleApiAuthenticationFailedException(ex: LeaderIdApiAuthenticationFailedException): ResponseEntity<ErrorDto> {
        logger.error("LeaderId API authentication failed: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for LeaderIdApiRateLimitExceededException.
     */
    @ExceptionHandler(LeaderIdApiRateLimitExceededException::class)
    fun handleApiRateLimitExceededException(ex: LeaderIdApiRateLimitExceededException): ResponseEntity<ErrorDto> {
        logger.warn("LeaderId API rate limit exceeded: {}", ex.message)
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for InvalidSearchCriteriaException.
     */
    @ExceptionHandler(InvalidSearchCriteriaException::class)
    fun handleInvalidSearchCriteriaException(ex: InvalidSearchCriteriaException): ResponseEntity<ErrorDto> {
        logger.warn("Invalid search criteria: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for InvalidEventIdException.
     */
    @ExceptionHandler(InvalidEventIdException::class)
    fun handleInvalidEventIdException(ex: InvalidEventIdException): ResponseEntity<ErrorDto> {
        logger.warn("Invalid event ID: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for EventsRetrievalFailedException.
     */
    @ExceptionHandler(EventsRetrievalFailedException::class)
    fun handleEventsRetrievalFailedException(ex: EventsRetrievalFailedException): ResponseEntity<ErrorDto> {
        logger.error("Events retrieval failed: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for EventsSearchFailedException.
     */
    @ExceptionHandler(EventsSearchFailedException::class)
    fun handleEventsSearchFailedException(ex: EventsSearchFailedException): ResponseEntity<ErrorDto> {
        logger.error("Events search failed: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Generic exception handler for LeaderIdException.
     * This handler catches any LeaderIdException that doesn't have a more specific handler.
     */
    @ExceptionHandler(LeaderIdException::class)
    fun handleLeaderIdException(ex: LeaderIdException): ResponseEntity<ErrorDto> {
        logger.error("LeaderId exception: {}", ex.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
}
