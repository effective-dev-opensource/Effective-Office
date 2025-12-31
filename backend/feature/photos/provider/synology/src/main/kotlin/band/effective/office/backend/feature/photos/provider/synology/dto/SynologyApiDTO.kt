package band.effective.office.backend.feature.photos.provider.synology.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Synology API response containing photos from album")
@JsonIgnoreProperties(ignoreUnknown = true)
data class SynologyApiResponseDTO(
    @JsonProperty("data")
    @Schema(description = "Photo data containing list of photos")
    val photoData: PhotoDataDTO,

    @JsonProperty("success")
    @Schema(description = "Indicates if the request was successful", example = "true")
    val success: Boolean
)

@Schema(description = "Synology photo data")
@JsonIgnoreProperties(ignoreUnknown = true)
data class PhotoDataDTO(
    @JsonProperty("list")
    @Schema(description = "List of photo information")
    val photosInfo: List<PhotoInfoDTO>
)

@Schema(description = "Synology photo information")
@JsonIgnoreProperties(ignoreUnknown = true)
data class PhotoInfoDTO(
    @JsonProperty("additional")
    @Schema(description = "Additional photo metadata")
    val additional: AdditionalDTO,

    @JsonProperty("id")
    @Schema(description = "Photo ID", example = "456")
    val id: Int,

    @JsonProperty("type")
    @Schema(description = "Photo type", example = "photo")
    val type: String
)

@Schema(description = "Additional photo metadata")
@JsonIgnoreProperties(ignoreUnknown = true)
data class AdditionalDTO(
    @JsonProperty("thumbnail")
    @Schema(description = "Thumbnail information")
    val thumbnail: ThumbnailDTO
)

@Schema(description = "Thumbnail information")
@JsonIgnoreProperties(ignoreUnknown = true)
data class ThumbnailDTO(
    @JsonProperty("cache_key")
    @Schema(description = "Thumbnail cache key", example = "abc123def456")
    val cacheKey: String,

    @JsonProperty("m")
    @Schema(description = "Medium thumbnail status", example = "ready")
    val sizeM: String,

    @JsonProperty("sm")
    @Schema(description = "Small thumbnail status", example = "ready")
    val sizeSm: String,

    @JsonProperty("xl")
    @Schema(description = "Extra large thumbnail status", example = "ready")
    val sizeXl: String
)