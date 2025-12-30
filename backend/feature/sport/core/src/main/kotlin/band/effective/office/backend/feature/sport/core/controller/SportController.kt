package band.effective.office.backend.feature.sport.core.controller

import band.effective.office.backend.feature.sport.core.dto.SportUserDTO
import band.effective.office.backend.feature.sport.core.dto.SportUserDtoMapper
import band.effective.office.backend.feature.sport.core.service.SportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for sport operations.
 * Provides endpoints for retrieving sport user information.
 */
@RestController
@RequestMapping("/v1/sport")
@Tag(name = "Sport", description = "Operations related to sport time tracking")
class SportController(
    private val sportService: SportService
) {

    /**
     * Retrieves sport users with their time tracking data.
     */
    @GetMapping
    @Operation(
        summary = "Get sport users",
        description = "Retrieves sport users from the configured provider with their time tracking data."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved sport users"),
        ]
    )
    fun getSportUsers(): ResponseEntity<List<SportUserDTO>> {
        val sportUsers = sportService.getSportUsers()
        val sportUserDTOs = SportUserDtoMapper.toSportUserDTOList(sportUsers)
        return ResponseEntity.ok(sportUserDTOs)
    }
}
