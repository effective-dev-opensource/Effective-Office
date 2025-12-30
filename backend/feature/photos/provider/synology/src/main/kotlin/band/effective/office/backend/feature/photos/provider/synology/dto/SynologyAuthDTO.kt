package band.effective.office.backend.feature.photos.provider.synology.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Synology authentication response")
@JsonIgnoreProperties(ignoreUnknown = true)
data class SynologyAuthResponseDTO(
    @JsonProperty("data")
    @Schema(description = "Authentication data containing session ID")
    val data: SynologyAuthDataDTO?,

    @JsonProperty("success")
    @Schema(description = "Indicates if authentication was successful", example = "true")
    val success: Boolean
)

@Schema(description = "Synology authentication data")
@JsonIgnoreProperties(ignoreUnknown = true)
data class SynologyAuthDataDTO(
    @JsonProperty("sid")
    @Schema(description = "Session ID for authenticated requests", example = "abc123def456")
    val sid: String?,

    @JsonProperty("did")
    @Schema(description = "Device ID for authenticated requests", example = "62xnvojLuM0AP_ollFLky1EkdOqhlu2v")
    val did: String?
)


