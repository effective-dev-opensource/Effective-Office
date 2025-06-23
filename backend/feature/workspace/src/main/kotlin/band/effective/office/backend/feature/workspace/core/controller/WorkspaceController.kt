package band.effective.office.backend.feature.workspace.core.controller

import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.model.WorkspaceZone
import band.effective.office.backend.feature.workspace.core.dto.WorkspaceDTO
import band.effective.office.backend.feature.workspace.core.dto.WorkspaceZoneDTO
import band.effective.office.backend.feature.workspace.core.service.WorkspaceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.Instant
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for managing workspaces.
 */
@RestController
@RequestMapping("/v1/workspaces")
@Tag(name = "Workspaces", description = "API for managing workspaces")
class WorkspaceController(
    private val workspaceService: WorkspaceService,
) {

    /**
     * Get a workspace by ID.
     *
     * @param id the workspace ID
     * @return the workspace if found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get workspace by ID",
        description = "Returns a workspace by its ID",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved workspace")
    @ApiResponse(responseCode = "404", description = "Workspace not found")
    fun getWorkspaceById(
        @Parameter(description = "Workspace ID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<WorkspaceDTO> {
        val workspace = workspaceService.findById(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(convertToDto(workspace))
    }

    /**
     * Get workspaces by tag, optionally filtering for free workspaces in a time period.
     *
     * @param tag the workspace tag
     * @param freeFrom start of the time range for free workspaces
     * @param freeUntil end of the time range for free workspaces
     * @return list of workspaces matching the criteria
     */
    @GetMapping
    @Operation(
        summary = "Get workspaces by tag",
        description = "Returns workspaces by tag, optionally filtering for free workspaces in a time period",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved workspaces")
    @ApiResponse(responseCode = "400", description = "Invalid parameters")
    fun getWorkspacesByTag(
        @Parameter(description = "Workspace tag", required = true, example = "meeting")
        @RequestParam(name = "workspace_tag") tag: String,

        @Parameter(description = "Start of the time range for free workspaces", example = "2023-01-01T00:00:00Z")
        @RequestParam(name = "free_from", required = false) freeFrom: Instant?,

        @Parameter(description = "End of the time range for free workspaces", example = "2023-01-01T02:00:00Z")
        @RequestParam(name = "free_until", required = false) freeUntil: Instant?
    ): ResponseEntity<List<WorkspaceDTO>> {
        val workspaces = workspaceService.findAllByTag(tag)
        /*val workspaces = if (freeFrom != null && freeUntil != null) {
            // TODO workspaceService.findAllFreeByPeriod(tag, freeFrom, freeUntil)
        } else {
            workspaceService.findAllByTag(tag)
        }*/

        return ResponseEntity.ok(workspaces.map { convertToDto(it) })
    }

    /**
     * Get all workspace zones.
     *
     * @return list of all workspace zones
     */
    @GetMapping("/zones")
    @Operation(
        summary = "Get all workspace zones",
        description = "Returns all workspace zones",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved workspace zones")
    fun getAllZones(): ResponseEntity<List<WorkspaceZoneDTO>> {
        val zones = workspaceService.findAllZones()
        return ResponseEntity.ok(zones.map { convertZoneToDto(it) })
    }

    /**
     * Convert a domain Workspace to a WorkspaceDTO.
     */
    private fun convertToDto(workspace: Workspace): WorkspaceDTO {
        return WorkspaceDTO(
            id = workspace.id.toString(),
            name = workspace.name,
            utilities = workspace.utilities.map { utility ->
                band.effective.office.backend.feature.workspace.core.dto.UtilityDTO(
                    id = utility.id.toString(),
                    name = utility.name,
                    iconUrl = utility.iconUrl,
                    count = utility.count
                )
            },
            zone = workspace.zone?.let { zone ->
                WorkspaceZoneDTO(
                    id = zone.id.toString(),
                    name = zone.name
                )
            },
            tag = workspace.tag
        )
    }

    /**
     * Convert a domain WorkspaceZone to a WorkspaceZoneDTO.
     */
    private fun convertZoneToDto(zone: WorkspaceZone): WorkspaceZoneDTO {
        return WorkspaceZoneDTO(
            id = zone.id.toString(),
            name = zone.name
        )
    }
}