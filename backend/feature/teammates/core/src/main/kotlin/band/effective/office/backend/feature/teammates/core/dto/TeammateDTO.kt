package band.effective.office.backend.feature.teammates.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Teammate information")
data class TeammateDTO(
    @Schema(description = "Unique identifier of the teammate", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: String,

    @Schema(description = "Full name of the teammate", example = "John Doe")
    val name: String,

    @Schema(description = "List of positions held by the teammate", example = "[\"Developer\", \"Team Lead\"]")
    val positions: List<String>,

    @Schema(description = "Employment type", example = "Band")
    val employment: String,

    @Schema(description = "Start date of employment", example = "2023-01-15")
    val startDate: LocalDate,

    @Schema(description = "Next birthday date", example = "1990-05-20")
    val nextBDay: LocalDate,

    @Schema(description = "Duolingo profile username", example = "john_duolingo")
    val duolingo: String? = null,

    @Schema(description = "Profile photo URL", example = "https://example.com/photo.jpg")
    val photo: String? = null,

    @Schema(description = "Current status", example = "Active")
    val status: String,

    @Schema(description = "Whether the teammate is currently active", example = "true")
    val isActive: Boolean
)