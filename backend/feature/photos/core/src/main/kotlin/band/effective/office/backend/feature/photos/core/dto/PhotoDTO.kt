package band.effective.office.backend.feature.photos.core.dto

import band.effective.office.backend.feature.photos.core.domain.model.Photo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Photo model")
@JsonIgnoreProperties(ignoreUnknown = true)
data class PhotoDTO(
    @JsonProperty("thumbnailUrl")
    @Schema(description = "URL to the photo thumbnail", example = "https://example.com/thumb.jpg")
    val thumbnailUrl: String
)

@Schema(description = "Response containing photos and metadata")
@JsonIgnoreProperties(ignoreUnknown = true)
data class PhotosResponseDTO(
    @JsonProperty("success")
    @Schema(description = "Indicates if the operation was successful", example = "true")
    val success: Boolean,

    @JsonProperty("message")
    @Schema(description = "Response message", example = "Photos retrieved successfully")
    val message: String,

    @JsonProperty("data")
    @Schema(description = "Photos data and metadata")
    val data: PhotosDataDTO? = null
)

@Schema(description = "Photos data with metadata")
@JsonIgnoreProperties(ignoreUnknown = true)
data class PhotosDataDTO(
    @JsonProperty("photos")
    @Schema(description = "List of photos")
    val photos: List<PhotoDTO>,

    @JsonProperty("totalCount")
    @Schema(description = "Total number of photos", example = "150")
    val totalCount: Int,

    @JsonProperty("limit")
    @Schema(description = "Maximum number of photos requested", example = "50")
    val limit: Int? = null
)

fun Photo.toDTO(): PhotoDTO {
    return PhotoDTO(
        thumbnailUrl = thumbnailUrl
    )
}