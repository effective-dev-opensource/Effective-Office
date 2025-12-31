package band.effective.office.backend.feature.leader.id.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * External API models for LeaderId integration.
 */
@Schema(description = "LeaderId search events response")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdSearchEventsResponse(
    @JsonProperty("data")
    @Schema(description = "Search data containing list of events")
    val data: LeaderIdSearchData?
)

@Schema(description = "LeaderId search data")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdSearchData(
    @JsonProperty("_items")
    @Schema(description = "List of event items")
    val items: List<LeaderIdSearchItem>?
)

@Schema(description = "LeaderId search item")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdSearchItem(
    @JsonProperty("id")
    @Schema(description = "Event ID", example = "12345")
    val id: Int
)

@Schema(description = "LeaderId event info response")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdEventInfoResponse(
    @JsonProperty("data")
    @Schema(description = "Event data containing detailed information")
    val data: LeaderIdEventData
)

@Schema(description = "LeaderId event data")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdEventData(
    @JsonProperty("id")
    @Schema(description = "Event ID", example = "12345")
    val id: Int,
    
    @JsonProperty("full_name")
    @Schema(description = "Full name of the event", example = "Tech Conference 2024")
    val fullName: String,
    
    @JsonProperty("date_start")
    @Schema(description = "Event start date", example = "2024-01-15T10:00:00Z")
    val dateStart: String,
    
    @JsonProperty("date_end")
    @Schema(description = "Event end date", example = "2024-01-15T18:00:00Z")
    val dateEnd: String,
    
    @JsonProperty("status")
    @Schema(description = "Event status", example = "active")
    val status: String,
    
    @JsonProperty("photo")
    @Schema(description = "Event photo URL", example = "https://example.com/event-photo.jpg")
    val photo: String?,
    
    @JsonProperty("organizers")
    @Schema(description = "List of event organizers")
    val organizers: List<LeaderIdOrganizerDTO>,
    
    @JsonProperty("speakers")
    @Schema(description = "List of event speakers")
    val speakers: List<LeaderIdSpeakerDTO>,
    
    @JsonProperty("registration_date_end")
    @Schema(description = "Registration end date", example = "2024-01-10T23:59:59Z")
    val registrationDateEnd: String?
)

@Schema(description = "LeaderId organizer")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdOrganizerDTO(
    @JsonProperty("name")
    @Schema(description = "Organizer name", example = "Tech Company")
    val name: String
)

@Schema(description = "LeaderId speaker")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdSpeakerDTO(
    @JsonProperty("user")
    @Schema(description = "Speaker user information")
    val user: LeaderIdUser
)

@Schema(description = "LeaderId user")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderIdUser(
    @JsonProperty("first_name")
    @Schema(description = "User first name", example = "John")
    val firstName: String,
    
    @JsonProperty("last_name")
    @Schema(description = "User last name", example = "Doe")
    val lastName: String,
    
    @JsonProperty("avatar")
    @Schema(description = "User avatar URL", example = "https://example.com/avatar.jpg")
    val avatar: String?
)
