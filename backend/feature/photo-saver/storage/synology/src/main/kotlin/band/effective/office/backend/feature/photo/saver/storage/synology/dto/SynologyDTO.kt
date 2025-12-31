package band.effective.office.backend.feature.photo.saver.storage.synology.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Synology API DTOs with Jackson annotations.
 */

data class SynologyAuthResponseDTO(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("data") val data: SynologyAuthDataDTO?,
    @JsonProperty("error") val error: SynologyErrorDTO?
)

data class SynologyAuthDataDTO(
    @JsonProperty("sid") val sid: String
)

data class SynologyAlbumsResponseDTO(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("data") val data: SynologyAlbumsDataDTO?,
    @JsonProperty("error") val error: SynologyErrorDTO?
)

data class SynologyAlbumsDataDTO(
    @JsonProperty("list") val albums: List<AlbumDTO>?
)

data class AlbumDTO(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String
)

data class SynologyCreateAlbumResponseDTO(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("data") val data: SynologyCreateAlbumDataDTO?,
    @JsonProperty("error") val error: SynologyErrorDTO?
)

data class SynologyCreateAlbumDataDTO(
    @JsonProperty("album") val album: CreatedAlbumDTO?
)

data class CreatedAlbumDTO(
    @JsonProperty("id") val id: Int
)

data class SynologyErrorDTO(
    @JsonProperty("code") val code: Int
)

data class SynologyUploadPhotoResponseDTO(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("data") val data: SynologyUploadPhotoDataDTO?,
    @JsonProperty("error") val error: SynologyErrorDTO?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SynologyUploadPhotoDataDTO(
    @JsonProperty("id") val id: Int?,
    @JsonProperty("unit_id") val unitId: Int?,
    @JsonProperty("action") val action: String?
)

data class SynologyAddPhotoResponseDTO(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("error") val error: SynologyErrorDTO?
)
