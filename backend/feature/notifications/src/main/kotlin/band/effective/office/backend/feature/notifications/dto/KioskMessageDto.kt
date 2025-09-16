package band.effective.office.backend.feature.notifications.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * API response for successful operations in notifications module.
 */
@Schema(description = "API response for successful operations")
data class KioskMessageDto(
    @Schema(
        description = "Message for the client",
        example = "Kiosk mode enabled for device: 7ac6ddd9a731bbeb"
    )
    val message: String
)