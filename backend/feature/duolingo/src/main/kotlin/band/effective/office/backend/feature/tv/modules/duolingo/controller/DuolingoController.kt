package band.effective.office.backend.feature.duolingo.controller

import band.effective.office.backend.feature.duolingo.dto.DuolingoResponseDTO
import band.effective.office.backend.feature.duolingo.service.DuolingoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for managing Duolingo user information.
 */
@RestController
@RequestMapping("/v1/duolingo")
@Tag(name = "Duolingo", description = "API for retrieving Duolingo user information")
class DuolingoController(
    private val duolingoService: DuolingoService
) {

    /**
     * Get Duolingo user data by usernames.
     *
     * @param usernames list of Duolingo usernames
     * @return Duolingo user information
     */
    @GetMapping("/users")
    @Operation(
        summary = "Get Duolingo user data",
        description = "Returns information about Duolingo users for the provided list of usernames"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user data")
    @ApiResponse(responseCode = "400", description = "Invalid usernames provided")
    fun getDuolingoUsers(
        @Parameter(description = "List of Duolingo usernames", required = true)
        @RequestParam("usernames") usernames: List<String>
    ): ResponseEntity<DuolingoResponseDTO> {
        val response = duolingoService.getUsersInfo(usernames)
        return ResponseEntity.ok(response)
    }
}