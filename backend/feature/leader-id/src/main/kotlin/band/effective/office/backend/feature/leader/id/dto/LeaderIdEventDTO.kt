package band.effective.office.backend.feature.leader.id.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * Data Transfer Object for LeaderId event information.
 */
@Schema(description = "LeaderId event information")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdEventDTO(
    @JsonProperty("id")
    @Schema(description = "Event ID", example = "12345")
    val id: Int,

    @JsonProperty("name")
    @Schema(description = "Event name", example = "Tech Conference 2024")
    val name: String,

    @JsonProperty("startDateTime")
    @Schema(description = "Event start date and time")
    val startDateTime: LocalDateTime,

    @JsonProperty("finishDateTime")
    @Schema(description = "Event finish date and time")
    val finishDateTime: LocalDateTime,

    @JsonProperty("isOnline")
    @Schema(description = "Whether the event is online", example = "true")
    val isOnline: Boolean,

    @JsonProperty("photoUrl")
    @Schema(description = "Event photo URL", example = "https://example.com/photo.jpg")
    val photoUrl: String?,

    @JsonProperty("organizer")
    @Schema(description = "Event organizer name", example = "Tech Company")
    val organizer: String?,

    @JsonProperty("speakers")
    @Schema(description = "List of event speakers")
    val speakers: List<String>?,

    @JsonProperty("endRegDate")
    @Schema(description = "Registration end date")
    val endRegDate: LocalDateTime?
)
