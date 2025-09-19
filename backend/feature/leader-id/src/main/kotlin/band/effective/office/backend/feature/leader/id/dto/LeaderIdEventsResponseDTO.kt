package band.effective.office.backend.feature.leader.id.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Response DTO containing a list of LeaderId events.
 */
@Schema(description = "Response containing LeaderId events")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdEventsResponseDTO(
    @JsonProperty("events")
    @Schema(description = "List of LeaderId events")
    val events: List<LeaderIdEventDTO>
)
