package band.effective.office.backend.feature.teammates.core.controller

import band.effective.office.backend.feature.teammates.core.dto.TeammateDTO
import band.effective.office.backend.feature.teammates.core.dto.TeammateDtoMapper
import band.effective.office.backend.feature.teammates.core.dto.TeammateScoreDTO
import band.effective.office.backend.feature.teammates.core.service.TeammateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for teammate operations.
 * Provides endpoints for retrieving teammate information.
 */
@RestController
@RequestMapping("/v1/teammates")
@Tag(name = "Teammates", description = "Operations related to teammates")
class TeammateController(
    private val teammateService: TeammateService
) {

    /**
     * Retrieves teammates, optionally filtering by active status.
     */
    @GetMapping
    @Operation(
        summary = "Get teammates",
        description = "Retrieves teammates from the configured provider. Use 'active=true' to filter only active teammates."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved teammates"),
        ]
    )
    fun getTeammates(
        @Parameter(description = "Filter only active teammates")
        @RequestParam(name = "active", required = false, defaultValue = "false") active: Boolean
    ): ResponseEntity<List<TeammateDTO>> {
        val teammates = teammateService.getTeammates(active)
        val teammateDTOs = TeammateDtoMapper.toTeammateDTOList(teammates)
        return ResponseEntity.ok(teammateDTOs)
    }

    /**
     * Retrieves teammate scores.
     */
    @GetMapping("/score")
    @Operation(
        summary = "Get teammate scores",
        description = "Retrieves teammate scores from the same Notion database."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved teammate scores"),
        ]
    )
    fun getTeammateScores(): ResponseEntity<List<TeammateScoreDTO>> {
        val scores = teammateService.getTeammateScores()
        val scoreDTOs = TeammateDtoMapper.toScoreDTOList(scores)
        return ResponseEntity.ok(scoreDTOs)
    }
}