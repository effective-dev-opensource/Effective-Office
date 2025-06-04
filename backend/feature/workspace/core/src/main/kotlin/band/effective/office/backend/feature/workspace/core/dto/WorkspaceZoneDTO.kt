package band.effective.office.backend.feature.workspace.core.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for a zone in the office where workspaces are located.
 */
@Schema(description = "Zone in the office where workspaces are located")
data class WorkspaceZoneDTO(
    @Schema(description = "Unique identifier of the workspace zone", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String,
    
    @Schema(description = "Name of the workspace zone", example = "Floor 1")
    val name: String
)