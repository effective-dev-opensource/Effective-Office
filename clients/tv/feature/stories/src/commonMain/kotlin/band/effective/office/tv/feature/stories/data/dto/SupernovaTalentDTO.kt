package band.effective.office.tv.feature.stories.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupernovaTalentDTO(
    @SerialName("id")
    val id: String,

    @SerialName("score")
    val score: Int,
)


