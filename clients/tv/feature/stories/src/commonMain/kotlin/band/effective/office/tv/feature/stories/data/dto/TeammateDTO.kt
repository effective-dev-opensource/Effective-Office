package band.effective.office.tv.feature.stories.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Teammate information DTO.
 */
@Serializable
data class TeammateDTO(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("positions")
    val positions: List<String>,

    @SerialName("employment")
    val employment: String,

    @SerialName("startDate")
    val startDate: String,

    @SerialName("nextBDay")
    val nextBDay: String,

    @SerialName("duolingo")
    val duolingo: String? = null,

    @SerialName("photo")
    val photo: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("status")
    val status: String,

    @SerialName("isActive")
    val isActive: Boolean,
)


