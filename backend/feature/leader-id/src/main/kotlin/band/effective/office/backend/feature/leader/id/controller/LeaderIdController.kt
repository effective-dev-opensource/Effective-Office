package band.effective.office.backend.feature.leader.id.controller

import band.effective.office.backend.feature.leader.id.dto.LeaderIdEventsResponseDTO
import band.effective.office.backend.feature.leader.id.service.LeaderIdService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for managing event information.
 * 
 * This controller provides endpoints for retrieving events
 * with proper API documentation, error handling, and logging.
 * It follows RESTful principles and provides clear API contracts.
 * Acts as a proxy to external event sources (LeaderId, etc.).
 */
@RestController
@RequestMapping("/v1/events")
@Tag(name = "Events", description = "API for retrieving event information")
class LeaderIdController(
    private val leaderIdService: LeaderIdService
) {

    private val logger = LoggerFactory.getLogger(LeaderIdController::class.java)

    /**
     * Get events for TV application.
     * 
     * This endpoint retrieves events from configured event sources
     *
     * @return ResponseEntity containing events information
     */
    @GetMapping
    @Operation(
        summary = "Get events for TV",
        description = "Returns events with default parameters for TV display",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved events data")
    fun getEventsForTv(): ResponseEntity<LeaderIdEventsResponseDTO> {
        logger.info("Received request for events")
        
        val response = leaderIdService.getEventsForTv()
        logger.info("Successfully processed request for events")
        return ResponseEntity.ok(response)
    }
}
