package band.effective.office.backend.feature.notifications.controller

import band.effective.office.backend.feature.notifications.service.INotificationSender
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class KioskToggleRequest(val enabled: Boolean)

@RestController
@RequestMapping("/api/v1/kiosk")
@Tag(name = "Kiosk", description = "API for managing kiosk mode")
class KioskController(private val notificationSender: INotificationSender) {

    @PostMapping("/toggle")
    @Operation(
        summary = "Toggle kiosk mode",
        description = "Sends a command to enable or disable kiosk mode on all tablets",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun toggleKiosk(@RequestBody request: KioskToggleRequest): ResponseEntity<String> {
        val payload = mapOf(
            "type" to "KIOSK_TOGGLE",
            "enabled" to request.enabled.toString()
        )
        notificationSender.sendDataMessage("kiosk-commands", payload)
        return ResponseEntity.ok().build()
    }
}