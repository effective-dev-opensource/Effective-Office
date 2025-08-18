package band.effective.office.backend.feature.tv.modules.duolingo.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for Duolingo current streak.
 */
@Schema(description = "Duolingo current streak")
data class CurrentStreakDTO(
    @JsonProperty("currentStreak")
    @Schema(description = "Details of the current streak")
    val currentStreak: StreakDetails? = null
) {
    /**
     * Nested data class for streak details.
     */
    @Schema(description = "Details of the streak")
    data class StreakDetails(
        @JsonProperty("endDate")
        @Schema(description = "End date of the streak", example = "2023-10-01")
        val endDate: String? = null,

        @JsonProperty("length")
        @Schema(description = "Length of the streak", example = "10")
        val length: Int? = null,

        @JsonProperty("startDate")
        @Schema(description = "Start date of the streak", example = "2023-09-20")
        val startDate: String? = null
    )
}