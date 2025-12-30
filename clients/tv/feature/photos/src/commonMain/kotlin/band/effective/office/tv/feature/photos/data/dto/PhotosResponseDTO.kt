package band.effective.office.tv.feature.photos.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponseDTO(
    @SerialName("success")
    val success: Boolean? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("data")
    val data: PhotosDataDTO? = null,
)

@Serializable
data class PhotosDataDTO(
    @SerialName("photos")
    val photos: List<PhotoDTO> = emptyList(),
    @SerialName("totalCount")
    val totalCount: Int? = null,
    @SerialName("limit")
    val limit: Int? = null,
)

@Serializable
data class PhotoDTO(
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String? = null,
)

