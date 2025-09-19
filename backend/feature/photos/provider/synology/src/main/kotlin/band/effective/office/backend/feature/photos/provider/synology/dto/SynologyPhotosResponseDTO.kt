package band.effective.office.backend.feature.photos.provider.synology.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Synology photo model")
@JsonIgnoreProperties(ignoreUnknown = true)
data class SynologyPhotoDTO(
    @JsonProperty("photoThumb")
    @Schema(description = "URL of the photo thumbnail", example = "https://your-synology.com/webapi/entry.cgi/?cache_key=abc123&id=123&api=SYNO.Foto.Thumbnail&method=get&version=1&type=unit&size=xl&_sid=xyz789")
    val photoThumb: String
)