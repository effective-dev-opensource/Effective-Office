package band.effective.office.backend.feature.sport.core.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Sport user information")
data class SportUserDTO(
    @Schema(description = "User name", example = "John Doe")
    val name: String,

    @Schema(description = "User email", example = "john.doe@example.com")
    val email: String,

    @Schema(description = "Total seconds spent on sport activities", example = "7200")
    val totalSeconds: Int
)
