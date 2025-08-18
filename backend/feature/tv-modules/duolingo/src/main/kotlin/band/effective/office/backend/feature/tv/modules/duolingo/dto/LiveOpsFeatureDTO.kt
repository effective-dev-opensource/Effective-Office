package band.effective.office.backend.feature.tv.modules.duolingo.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for Duolingo live ops feature.
 */
@Schema(description = "Duolingo live ops feature")
data class LiveOpsFeatureDTO(
    @JsonProperty("endTimestamp")
    @Schema(description = "End timestamp of the feature", example = "1696118400")
    val endTimestamp: Int? = null,

    @JsonProperty("startTimestamp")
    @Schema(description = "Start timestamp of the feature", example = "1693526400")
    val startTimestamp: Int? = null,

    @JsonProperty("type")
    @Schema(description = "Type of the feature", example = "event")
    val type: String? = null
)