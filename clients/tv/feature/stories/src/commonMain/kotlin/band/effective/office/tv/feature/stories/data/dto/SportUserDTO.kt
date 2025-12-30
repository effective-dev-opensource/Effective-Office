package band.effective.office.tv.feature.stories.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for sport time user data from backend API.
 */
@Serializable
data class SportUserDTO(
    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("totalSeconds")
    val totalSeconds: Int,
)


