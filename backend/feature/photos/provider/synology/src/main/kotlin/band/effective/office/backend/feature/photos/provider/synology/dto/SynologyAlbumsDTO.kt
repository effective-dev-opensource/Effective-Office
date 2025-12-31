package band.effective.office.backend.feature.photos.provider.synology.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Synology albums response")
@JsonIgnoreProperties(ignoreUnknown = true)
data class SynologyAlbumsResponseDTO(
    @JsonProperty("data")
    @Schema(description = "Albums data containing list of albums")
    val albumsData: AlbumsDataDTO?,

    @JsonProperty("success")
    @Schema(description = "Indicates if the request was successful", example = "true")
    val success: Boolean
)

@Schema(description = "Synology albums data")
@JsonIgnoreProperties(ignoreUnknown = true)
data class AlbumsDataDTO(
    @JsonProperty("list")
    @Schema(description = "List of Synology albums")
    val albums: List<AlbumDTO>
)

@Schema(description = "Synology album information")
@JsonIgnoreProperties(ignoreUnknown = true)
data class AlbumDTO(
    @JsonProperty("id")
    @Schema(description = "Album ID", example = "123")
    val id: Int,

    @JsonProperty("name")
    @Schema(description = "Album name", example = "Best of 2024")
    val name: String,

    @JsonProperty("item_count")
    @Schema(description = "Number of items in the album", example = "150")
    val itemCount: Int
)