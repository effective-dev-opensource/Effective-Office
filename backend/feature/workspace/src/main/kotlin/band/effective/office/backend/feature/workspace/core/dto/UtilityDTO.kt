package band.effective.office.backend.feature.workspace.core.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for a utility available in a workspace.
 */
@Schema(description = "Utility available in a workspace")
data class UtilityDTO(
    @Schema(description = "Unique identifier of the utility", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String,
    
    @Schema(description = "Name of the utility", example = "Monitor")
    val name: String,
    
    @Schema(description = "URL for the utility's icon", example = "https://example.com/icons/monitor.png")
    val iconUrl: String,
    
    @Schema(description = "Quantity of this utility", example = "2")
    val count: Int
)