package band.effective.office.tv.feature.stories.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DuolingoResponseDTO(
    @SerialName("users")
    val users: List<DuolingoUserDTO>,
)

@Serializable
data class DuolingoUserDTO(
    @SerialName("username")
    val username: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("picture")
    val picture: String? = null,

    @SerialName("streak")
    val streak: Int? = null,

    @SerialName("totalXp")
    val totalXp: Int? = null,

    @SerialName("courses")
    val courses: List<DuolingoCourseDTO>? = null,
)

@Serializable
data class DuolingoCourseDTO(
    @SerialName("learningLanguage")
    val learningLanguage: String? = null,
)


