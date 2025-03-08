package band.effective.office.tv.network.leader.models.eventInfo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Speaker(
    val completed: Boolean?,
    @Json(name = "completed_at")
    val completedAt: Any?,
    @Json(name = "control_passed")
    val controlPassed: Any?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "event_id")
    val eventId: Int?,
    val format: Any?,
    val id: String?,
    val moderation: String?,
    @Json(name = "number_participants")
    val numberParticipants: Any?,
    @Json(name = "quiz_answer_id")
    val quizAnswerId: Any?,
    @Json(name = "rejected_by")
    val rejectedBy: Any?,
    val role: String?,
    @Json(name = "role_id")
    val roleId: Int?,
    @Json(name = "")
    val spaceId: Any?,
    val status: Int?,
    @Json(name = "team_id")
    val teamId: Any?,
    @Json(name = "updated_at")
    val updatedAt: Any?,
    val user: User,
    @Json(name = "user_id")
    val userId: Int?,
    val visible: Boolean?,
    @Json(name = "w_group_ids")
    val wGroupIds: Any?
)