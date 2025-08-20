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
     * Enables kiosk mode for a specific device or all devices.
     */
    @PostMapping("/enable")
    @Operation(
        summary = "Enable kiosk mode",
        description = "Enables kiosk mode for a specific device or all devices if deviceId is not provided",
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
        responseCode = "404",
        description = "Device not found",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorDto::class)
        )]
    )
    fun enableKiosk(@Valid @RequestBody request: KioskToggleRequest): ResponseEntity<Any> {
        return toggleKiosk(request, true)
    }

    /**
     * Disables kiosk mode for a specific device or all devices.
     */
    @PostMapping("/disable")
    @Operation(
        summary = "Disable kiosk mode",
        description = "Disables kiosk mode for a specific device or all devices if deviceId is not provided",
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
        responseCode = "404",
        description = "Device not found",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorDto::class)
        )]
    )
    fun disableKiosk(@Valid @RequestBody request: KioskToggleRequest): ResponseEntity<Any> {
        return toggleKiosk(request, false)
    }

    /**
     * Toggles kiosk mode for a specific device or all devices.
     *
     * @param request The toggle request containing the device ID (optional).
     * @param enabled Whether to enable or disable kiosk mode.
     * @return Response with success or error message.
     */
    private fun toggleKiosk(request: KioskToggleRequest, enabled: Boolean): ResponseEntity<Any> {
        if (request.deviceId != null && !deviceService.deviceExists(request.deviceId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto(message = "Device with ID ${request.deviceId} not found", code = 404))
        }

        val payload = mapOf(
            "type" to MESSAGE_TYPE,
            "enabled" to enabled.toString(),
            *(request.deviceId?.let { arrayOf("deviceId" to it) } ?: emptyArray())
        )

        return try {
            notificationSender.sendDataMessage(KIOSK_TOPIC, payload)
            val messageText = if (request.deviceId != null) {
                "Kiosk mode ${if (enabled) "enabled" else "disabled"} for device: ${request.deviceId}"
            } else {
                "Kiosk mode ${if (enabled) "enabled" else "disabled"} for all devices"
            }
            ResponseEntity.ok(KioskMessageDto(messageText))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorDto(message = "Failed to send kiosk command: ${e.message}", code = 500))
        }
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