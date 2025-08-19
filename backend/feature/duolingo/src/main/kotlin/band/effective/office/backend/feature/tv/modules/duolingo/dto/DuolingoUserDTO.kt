package band.effective.office.backend.feature.duolingo.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for a Duolingo user.
 */
@Schema(description = "Duolingo user information")
@JsonIgnoreProperties(ignoreUnknown = true)
data class DuolingoUserDTO(
    @JsonProperty("username")
    @Schema(description = "Username of the Duolingo user", example = "username123")
    val username: String?,

    @JsonProperty("name")
    @Schema(description = "Name of the user", example = "John Doe")
    val name: String?,

    @JsonProperty("picture")
    @Schema(description = "URL of the user's profile picture", example = "https://duolingo.com/pictures/user123.png")
    val picture: String?,

    @JsonProperty("streak")
    @Schema(description = "Current streak of the user", example = "10")
    val streak: Int?,

    @JsonProperty("totalXp")
    @Schema(description = "Total experience points of the user", example = "5000")
    val totalXp: Int?,

    @JsonProperty("courses")
    @Schema(description = "List of courses the user is enrolled in")
    val courses: List<Map<String, Any>>?
)