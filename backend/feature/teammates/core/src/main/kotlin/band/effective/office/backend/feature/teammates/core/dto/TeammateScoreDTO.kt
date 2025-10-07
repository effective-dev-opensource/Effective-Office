package band.effective.office.backend.feature.teammates.core.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Teammate score information")
data class TeammateScoreDTO(
    @Schema(description = "Unique identifier of the teammate", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: String,

    @Schema(description = "Current score", example = "150")
    val score: Int
)
