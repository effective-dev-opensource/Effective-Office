package band.effective.office.backend.feature.notifications.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import band.effective.office.backend.feature.notifications.service.INotificationSender
import org.slf4j.LoggerFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

/**
 * Controller for Google calendar push notifications
 */
@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "API for handling notifications")
class CalendarNotificationsController(
    private val notificationSender: INotificationSender
) {
    private val logger = LoggerFactory.getLogger(CalendarNotificationsController::class.java)

    /**
     * Endpoint for receiving Google calendar push notifications
     */
    @PostMapping
    @Operation(
        summary = "Receive Google calendar push notification",
        description = "Endpoint for receiving push notifications from Google Calendar"
    )
    fun receiveNotification(@RequestBody(required = false) payload: String?): ResponseEntity<Void> {
        logger.info("Received push notification: \n{}", payload)
        notificationSender.sendEmptyMessage("booking")
        return ResponseEntity.ok().build()
    }
}