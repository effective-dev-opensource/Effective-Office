package band.effective.office.backend.feature.notifications.dto

import band.effective.office.backend.feature.notifications.repository.entity.DeviceEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

/**
 * Data Transfer Object for device information.
 */
@Schema(description = "Device information")
data class DeviceDto(
    @Schema(description = "Unique device identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,

    @Schema(description = "Android device ID", example = "7ac6ddd9a731bbeb")
    val deviceId: String,

    @Schema(description = "Device tag (e.g., meeting room name)", example = "Meeting Room A")
    val tag: String,

    @Schema(description = "Device registration timestamp")
    val createdAt: LocalDateTime
) {
    companion object {
        /**
         * Creates a DeviceDto from a DeviceEntity.
         */
        fun fromEntity(entity: DeviceEntity): DeviceDto {
            return DeviceDto(
                id = entity.id,
                deviceId = entity.deviceId,
                tag = entity.tag,
                createdAt = entity.createdAt
            )
        }
    }
}