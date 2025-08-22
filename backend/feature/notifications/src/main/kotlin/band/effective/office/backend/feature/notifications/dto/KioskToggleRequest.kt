package band.effective.office.backend.feature.notifications.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for kiosk mode toggle requests.
 *
 * This request allows enabling or disabling kiosk mode for a specific device
 */
@Schema(description = "Request to toggle kiosk mode")
data class KioskToggleRequest(
    @Schema(
        description = "Unique Android device ID",
        example = "7ac6ddd9a731bbeb",
        required = false
    )
    val deviceId: String
)