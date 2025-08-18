package band.effective.office.backend.feature.tv.modules.duolingo.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for a Duolingo user.
 */
@Schema(description = "Duolingo user information")
@JsonIgnoreProperties(ignoreUnknown = true) // Добавь эту аннотацию
data class DuolingoUserDTO(
    @JsonProperty("username")
    @Schema(description = "Username of the Duolingo user", example = "user123")
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

    @JsonProperty("hasPlus")
    @Schema(description = "Whether the user has a Duolingo Plus subscription", example = "true")
    val hasPlus: Boolean?,

    @JsonProperty("creationDate")
    @Schema(description = "User account creation timestamp", example = "1625097600")
    val creationDate: Int?,

    @JsonProperty("courses")
    @Schema(description = "List of courses the user is enrolled in")
    val courses: List<Map<String, Any>>?,

    @JsonProperty("streakData")
    @Schema(description = "User's streak data")
    val streakData: CurrentStreakDTO?,

    @JsonProperty("_achievements")
    @Schema(description = "Internal achievements data")
    val achievementsInternal: List<Any>? = null,

    @JsonProperty("achievements")
    @Schema(description = "User achievements")
    val achievements: List<Any>? = null,

    @JsonProperty("acquisitionSurveyReason")
    @Schema(description = "Reason for acquisition survey")
    val acquisitionSurveyReason: String? = null,

    @JsonProperty("betaStatus")
    @Schema(description = "Beta status of the user")
    val betaStatus: String? = null,

    @JsonProperty("bio")
    @Schema(description = "User biography")
    val bio: String? = null,

    @JsonProperty("canUseModerationTools")
    @Schema(description = "Whether the user can use moderation tools", example = "false")
    val canUseModerationTools: Boolean? = null,

    @JsonProperty("classroomLeaderboardsEnabled")
    @Schema(description = "Whether classroom leaderboards are enabled", example = "false")
    val classroomLeaderboardsEnabled: Boolean? = null,

    @JsonProperty("currentCourseId")
    @Schema(description = "ID of the current course", example = "DUO_EN")
    val currentCourseId: String? = null,

    @JsonProperty("emailVerified")
    @Schema(description = "Whether the user's email is verified", example = "true")
    val emailVerified: Boolean? = null,

    @JsonProperty("fromLanguage")
    @Schema(description = "Source language of the user", example = "en")
    val fromLanguage: String? = null,

    @JsonProperty("globalAmbassadorStatus")
    @Schema(description = "Global ambassador status")
    val globalAmbassadorStatus: Map<String, Any?>? = null,

    @JsonProperty("hasFacebookId")
    @Schema(description = "Whether the user has a linked Facebook ID", example = "false")
    val hasFacebookId: Boolean? = null,

    @JsonProperty("hasGoogleId")
    @Schema(description = "Whether the user has a linked Google ID", example = "false")
    val hasGoogleId: Boolean? = null,

    @JsonProperty("hasPhoneNumber")
    @Schema(description = "Whether the user has a linked phone number", example = "false")
    val hasPhoneNumber: Boolean? = null,

    @JsonProperty("hasRecentActivity15")
    @Schema(description = "Whether the user has recent activity", example = "true")
    val hasRecentActivity15: Boolean? = null,

    @JsonProperty("id")
    @Schema(description = "User ID", example = "123456")
    val id: Int? = null,

    @JsonProperty("joinedClassroomIds")
    @Schema(description = "List of classroom IDs the user has joined")
    val joinedClassroomIds: List<Any>? = null,

    @JsonProperty("learningLanguage")
    @Schema(description = "Language the user is learning", example = "es")
    val learningLanguage: String? = null,

    @JsonProperty("liveOpsFeatures")
    @Schema(description = "List of live ops features")
    val liveOpsFeatures: List<LiveOpsFeatureDTO>? = null,

    @JsonProperty("motivation")
    @Schema(description = "User's motivation")
    val motivation: String? = null,

    @JsonProperty("observedClassroomIds")
    @Schema(description = "List of observed classroom IDs")
    val observedClassroomIds: List<Any>? = null,

    @JsonProperty("privacySettings")
    @Schema(description = "User's privacy settings")
    val privacySettings: List<Any>? = null,

    @JsonProperty("profileCountry")
    @Schema(description = "User's profile country", example = "US")
    val profileCountry: String? = null,

    @JsonProperty("roles")
    @Schema(description = "List of user roles")
    val roles: List<String>? = null,

    @JsonProperty("shakeToReportEnabled")
    @Schema(description = "Whether shake to report is enabled")
    val shakeToReportEnabled: Any? = null,

    @JsonProperty("shouldForceConnectPhoneNumber")
    @Schema(description = "Whether the user should connect a phone number", example = "false")
    val shouldForceConnectPhoneNumber: Boolean? = null
)