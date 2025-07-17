package band.effective.office.backend.feature.notifications.controller

import band.effective.office.backend.feature.calendar.subscription.repository.ChannelRepository
import band.effective.office.backend.feature.calendar.subscription.service.GoogleCalendarService
import band.effective.office.backend.feature.notifications.service.INotificationSender
import com.google.api.client.util.DateTime
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.ExperimentalTime
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for Google calendar push notifications
 */
@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "API for handling notifications")
class CalendarNotificationsController(
    private val notificationSender: INotificationSender,
    private val deduplicator: NotificationDeduplicator,
    private val channelRepository: ChannelRepository,
    private val googleCalendarService: GoogleCalendarService,
) {
    private val logger = LoggerFactory.getLogger(CalendarNotificationsController::class.java)

    // Set to store previously seen message numbers for deduplication
    private val processedMessageNumbers = mutableSetOf<String>()

    /**
     * Endpoint for receiving Google calendar push notifications
     */
    @OptIn(ExperimentalTime::class)
    @PostMapping
    @Operation(
        summary = "Receive Google calendar push notification",
        description = "Endpoint for receiving push notifications from Google Calendar"
    )
    fun receiveNotification(
        @RequestBody(required = false) payload: String?,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        val channelId = request.getHeader("X-Goog-Channel-ID") ?: return ResponseEntity.ok().build()
        val resourceState = request.getHeader("X-Goog-Resource-State") ?: return ResponseEntity.ok().build()

        if (resourceState != "exists") return ResponseEntity.ok().build()

        val calendarId = channelRepository.findByChannelId(channelId)?.calendarId
            ?: return ResponseEntity.ok().build()

        val updatedEvents = fetchRecentlyUpdatedEvents(calendarId)

        for (event in updatedEvents) {
            val eventId = event.id

            if (eventId != null && !deduplicator.isDuplicate(eventId)) {
                logger.info("Triggering FCM push for event: ${event.summary}")
                notificationSender.sendEmptyMessage("effectiveoffice-booking")
            } else {
                logger.info("Duplicate event ignored: $eventId")
            }
        }
        return ResponseEntity.ok().build()
    }

    private fun fetchRecentlyUpdatedEvents(
        calendarId: String
    ): List<com.google.api.services.calendar.model.Event> {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val updatedMin = now.minusMinutes(2)

        return googleCalendarService.createCalendarService().events()
            .list(calendarId)
            .setShowDeleted(false)
            .setSingleEvents(true)
            .setMaxResults(10)
            .setOrderBy("updated")
            .setUpdatedMin(DateTime(updatedMin.toInstant().toEpochMilli()))
            .execute()
            .items
    }
}
