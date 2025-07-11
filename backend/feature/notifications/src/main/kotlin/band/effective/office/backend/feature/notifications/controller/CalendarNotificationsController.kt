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
import jakarta.servlet.http.HttpServletRequest

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

    // Set to store previously seen message numbers for deduplication
    private val processedMessageNumbers = mutableSetOf<String>()

    /**
     * Endpoint for receiving Google calendar push notifications
     */
    @PostMapping
    @Operation(
        summary = "Receive Google calendar push notification",
        description = "Endpoint for receiving push notifications from Google Calendar"
    )
    fun receiveNotification(
        @RequestBody(required = false) payload: String?,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        // Extract headers for deduplication
        val messageNumber = request.getHeader("X-Goog-Message-Number")
        val resourceState = request.getHeader("X-Goog-Resource-State")

        logger.info("Received push notification: messageNumber={}, resourceState={}, payload=\n{}", 
            messageNumber, resourceState, payload)

        // Check if this is a duplicate notification
        if (messageNumber != null) {
            if (processedMessageNumbers.contains(messageNumber)) {
                logger.info("Skipping duplicate notification with message number: {}", messageNumber)
                return ResponseEntity.ok().build()
            }

            // Add to processed set for future deduplication
            processedMessageNumbers.add(messageNumber)

            // Limit the size of the set to prevent memory leaks
            if (processedMessageNumbers.size > 1000) {
                // Remove the oldest entries (assuming they're added in order)
                val toRemove = processedMessageNumbers.size - 1000
                processedMessageNumbers.toList().take(toRemove).forEach { 
                    processedMessageNumbers.remove(it) 
                }
            }
        } else {
            logger.warn("Received notification without X-Goog-Message-Number header")
        }

        // Process the notification
        // Note: For duplicate notifications, we've already returned at line 51,
        // so this code is only executed for non-duplicate notifications

        // Only send message if resourceState is "exists"
        if (resourceState == "exists") {
            notificationSender.sendEmptyMessage("booking")
        } else {
            logger.info("Skipping notification with resourceState: {}", resourceState)
        }

        return ResponseEntity.ok().build()
    }
}
