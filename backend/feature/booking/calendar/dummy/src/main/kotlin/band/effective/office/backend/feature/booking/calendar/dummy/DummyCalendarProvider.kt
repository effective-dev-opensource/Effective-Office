package band.effective.office.backend.feature.booking.calendar.dummy

import band.effective.office.backend.feature.booking.core.domain.model.Booking
import band.effective.office.backend.feature.booking.core.domain.CalendarProvider
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * A dummy implementation of the CalendarProvider interface for testing purposes.
 * This implementation stores bookings in memory and doesn't interact with any external calendar service.
 */
@Component("dummyCalendarProvider")
@ConditionalOnProperty(name = ["calendar.provider"], havingValue = "dummy")
class DummyCalendarProvider : CalendarProvider {

    private val logger = LoggerFactory.getLogger(DummyCalendarProvider::class.java)
    private val bookings = ConcurrentHashMap<String, Booking>()

    override fun createEvent(booking: Booking): Booking {
        logger.debug("Creating dummy event for booking: {}", booking)

        // Generate a dummy external event ID
        val externalEventId = "dummy-event-${UUID.randomUUID()}"

        // Store the booking with the external event ID
        val bookingWithExternalId = booking.copy(id = externalEventId)
        bookings[externalEventId] = bookingWithExternalId

        logger.debug("Created dummy event with ID: {}", externalEventId)
        return bookingWithExternalId
    }

    override fun updateEvent(booking: Booking): Booking {
        logger.debug("Updating dummy event for booking: {}", booking)

        val externalEventId = booking.id

        // Check if the booking exists
        if (!bookings.containsKey(externalEventId)) {
            throw IllegalArgumentException("Booking with external event ID $externalEventId not found")
        }

        // Update the booking
        bookings[externalEventId] = booking

        logger.debug("Updated dummy event with ID: {}", externalEventId)
        return booking
    }

    override fun deleteEvent(booking: Booking) {
        logger.debug("Deleting dummy event for booking: {}", booking)

        val externalEventId = booking.id
        // Remove the booking
        bookings.remove(externalEventId)

        logger.debug("Deleted dummy event with ID: {}", externalEventId)
    }

    override fun findEventsByWorkspace(workspaceId: UUID, from: Instant, to: Instant?, returnInstances: Boolean): List<Booking> {
        logger.debug(
            "Finding dummy events for workspace with ID {} from {} to {}, returnInstances: {}",
            workspaceId,
            from,
            to ?: "infinity",
            returnInstances
        )

        // In this dummy implementation, we don't have recurring events, so returnInstances has no effect
        return bookings.values.filter { booking ->
            booking.workspace.id == workspaceId &&
            booking.beginBooking >= from &&
            (to == null || booking.endBooking <= to)
        }
    }

    override fun findEventsByUser(userId: UUID, from: Instant, to: Instant?, returnInstances: Boolean): List<Booking> {
        logger.debug(
            "Finding dummy events for user with ID {} from {} to {}, returnInstances: {}",
            userId,
            from,
            to ?: "infinity",
            returnInstances
        )

        // In this dummy implementation, we don't have recurring events, so returnInstances has no effect
        return bookings.values.filter { booking ->
            (booking.owner?.id == userId || booking.participants.any { it.id == userId }) &&
            booking.beginBooking >= from &&
            (to == null || booking.endBooking <= to)
        }
    }

    override fun findEventById(id: String): Booking? {
        logger.debug("Finding dummy event with ID {}", id)

        // In a real implementation, we would map the booking ID to the external event ID
        // For simplicity in this dummy implementation, we'll just search all bookings
        return bookings.values.find { it.id == id }
    }

    override fun findAllEvents(from: Instant, to: Instant?, returnInstances: Boolean): List<Booking> {
        logger.debug(
            "Finding all dummy events from {} to {}, returnInstances: {}",
            from,
            to ?: "infinity",
            returnInstances
        )

        // In this dummy implementation, we don't have recurring events, so returnInstances has no effect
        return bookings.values.filter { booking ->
            booking.beginBooking >= from &&
            (to == null || booking.endBooking <= to)
        }
    }
}
