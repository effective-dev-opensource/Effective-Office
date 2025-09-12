package band.effective.office.backend.feature.notifications.controller

import band.effective.office.backend.core.data.ErrorDto
import band.effective.office.backend.feature.notifications.dto.DeviceDto
import band.effective.office.backend.feature.notifications.dto.KioskMessageDto
import band.effective.office.backend.feature.notifications.dto.KioskToggleRequest
import band.effective.office.backend.feature.notifications.service.DeviceService
import band.effective.office.backend.feature.notifications.service.INotificationSender
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for managing kiosk mode.
 */
@RestController
@RequestMapping("/api/v1/kiosk")
@Tag(name = "Kiosk", description = "API for managing kiosk mode")
class KioskController(
    private val notificationSender: INotificationSender,
    private val deviceService: DeviceService
) {
    companion object {
        private const val KIOSK_TOPIC = "kiosk-commands"
        private const val MESSAGE_TYPE = "KIOSK_TOGGLE"
    }

    /**
     * Enables kiosk mode for a specific device.
     */
    @PostMapping("/device/enable")
    @Operation(
        summary = "Enable kiosk mode for specific device",
        description = "Enables kiosk mode for a specific device by deviceId",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Command sent successfully")
    @ApiResponse(
        responseCode = "404",
        description = "Device not found",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorDto::class)
        )]
    )
    fun enableKioskForDevice(@Valid @RequestBody request: KioskToggleRequest): ResponseEntity<Any> {
        return toggleKioskForDevice(request.deviceId, true)
    }

    /**
     * Disables kiosk mode for a specific device.
     */
    @PostMapping("/device/disable")
    @Operation(
        summary = "Disable kiosk mode for specific device",
        description = "Disables kiosk mode for a specific device by deviceId",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Command sent successfully")
    @ApiResponse(
        responseCode = "404",
        description = "Device not found",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorDto::class)
        )]
    )
    fun disableKioskForDevice(@Valid @RequestBody request: KioskToggleRequest): ResponseEntity<Any> {
        return toggleKioskForDevice(request.deviceId, false)
    }

    /**
     * Enables kiosk mode for all registered devices.
     */
    @PostMapping("/all/enable")
    @Operation(
        summary = "Enable kiosk mode for all devices",
        description = "Enables kiosk mode for all registered devices in the database",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(
        responseCode = "200",
        description = "Command sent successfully",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = KioskMessageDto::class)
        )]
    )
    @ApiResponse(
        responseCode = "400",
        description = "No devices found in database",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorDto::class)
        )]
    )
    fun enableKioskForAllDevices(): ResponseEntity<Any> {
        return toggleKioskForAllDevices(true)
    }

    /**
     * Disables kiosk mode for all registered devices.
     */
    @PostMapping("/all/disable")
    @Operation(
        summary = "Disable kiosk mode for all devices",
        description = "Disables kiosk mode for all registered devices in the database",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(
        responseCode = "200",
        description = "Command sent successfully",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = KioskMessageDto::class)
        )]
    )
    @ApiResponse(
        responseCode = "400",
        description = "No devices found in database",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorDto::class)
        )]
    )
    fun disableKioskForAllDevices(): ResponseEntity<Any> {
        return toggleKioskForAllDevices(false)
    }

    /**
     * Toggles kiosk mode for a specific device.
     */
    private fun toggleKioskForDevice(deviceId: String, isKioskModeActive: Boolean): ResponseEntity<Any> {
        if (!deviceService.deviceExists(deviceId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto(message = "Device with ID $deviceId not found", code = 404))
        }

        val payload = buildMap {
            put("type", MESSAGE_TYPE)
            put("isKioskModeActive", isKioskModeActive.toString())
            put("deviceId", deviceId)
        }

        notificationSender.sendDataMessage(KIOSK_TOPIC, payload)

        val messageText = "Kiosk mode ${if (isKioskModeActive) "enabled" else "disabled"} for device: $deviceId"
        return ResponseEntity.ok(KioskMessageDto(messageText))
    }

    /**
     * Toggles kiosk mode for all registered devices.
     * Sends one command with all device IDs from the database.
     */
    private fun toggleKioskForAllDevices(isKioskModeActive: Boolean): ResponseEntity<Any> {
        val allDevices = deviceService.getAllDevices()

        if (allDevices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto(message = "No devices found in database", code = 400))
        }

        val deviceIds = allDevices.map { it.deviceId }

        val payload = buildMap {
            put("type", MESSAGE_TYPE)
            put("isKioskModeActive", isKioskModeActive.toString())
            put("deviceIds", deviceIds.joinToString(","))
        }

        notificationSender.sendDataMessage(KIOSK_TOPIC, payload)

        val deviceCount = allDevices.size
        val messageText = "Kiosk mode ${if (isKioskModeActive) "enabled" else "disabled"} for $deviceCount devices"
        return ResponseEntity.ok(KioskMessageDto(messageText))
    }

    /**
     * Retrieves a list of all registered devices.
     */
    @GetMapping("/devices")
    @Operation(
        summary = "Get all devices",
        description = "Returns a list of all registered devices",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "List of devices retrieved successfully")
    fun getAllDevices(): ResponseEntity<List<DeviceDto>> {
        val devices = deviceService.getAllDevices().map { DeviceDto.fromEntity(it) }
        return ResponseEntity.ok(devices)
    }
}