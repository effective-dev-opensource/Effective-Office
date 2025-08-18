package band.effective.office.backend.feature.tv.modules.duolingo.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for Duolingo response containing user information.
 */
@Schema(description = "Response containing list of Duolingo users")
data class DuolingoResponseDTO(
    @JsonProperty("users")
    @Schema(description = "List of Duolingo users")
    val users: List<DuolingoUserDTO>
)