package band.effective.office.backend.feature.workspace.core.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for a workspace in the office.
 */
@Schema(description = "Workspace in the office")
data class WorkspaceDTO(
    @Schema(description = "Unique identifier of the workspace", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String,
    
    @Schema(description = "Name of the workspace", example = "Meeting Room 1")
    val name: String,
    
    @Schema(description = "List of utilities available in the workspace")
    val utilities: List<UtilityDTO>,
    
    @Schema(description = "Zone where the workspace is located")
    val zone: WorkspaceZoneDTO? = null,
    
    @Schema(description = "Tag for categorizing the workspace", example = "meeting")
    val tag: String
)