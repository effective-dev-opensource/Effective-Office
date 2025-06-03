package band.effective.office.backend.feature.booking.calendar.google

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.feature.booking.core.domain.CalendarProvider
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import band.effective.office.backend.feature.booking.core.domain.model.Workspace
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import java.time.Instant
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Google Calendar implementation of the CalendarProvider interface.
 */
@Component("googleCalendarProvider")
@ConditionalOnProperty(name = ["calendar.provider"], havingValue = "google")
class GoogleCalendarProvider(
    private val calendar: Calendar,
    private val calendarIdProvider: CalendarIdProvider
) : CalendarProvider {

    private val logger = LoggerFactory.getLogger(GoogleCalendarProvider::class.java)

    @Value("\${calendar.default-calendar}")
    private lateinit var defaultCalendar: String

    override fun createEvent(booking: Booking): Booking {
        logger.debug("Creating event for booking: {}", booking)

        val workspaceCalendarId = getCalendarIdByWorkspace(booking.workspace.id)
        logger.debug("workspaceCalendarId: {}", workspaceCalendarId)
        val event = convertToGoogleEvent(booking)
        logger.debug("event: {}", event)

        val savedEvent = runCatching {
            calendar.events().insert(defaultCalendar, event).execute()
        }.onFailure {
            logger.error("Failed to create event", it)
            return@onFailure
        }.getOrNull()
        if (savedEvent == null) throw NullPointerException("Failed to create event")

        logger.debug("savedEvent: {}", savedEvent)

        // Check if the event was successfully created in the workspace calendar
        if (!checkEventAvailability(savedEvent, workspaceCalendarId)) {
            // If not available, delete the event and throw an exception
            deleteEventById(savedEvent.id)
            throw WorkspaceUnavailableException("Workspace ${booking.workspace.name} is unavailable at the requested time")
        }

        // Return the booking with the external event ID set
        return booking.copy(externalEventId = savedEvent.id)
    }

    override fun updateEvent(booking: Booking): Booking {
        logger.debug("Updating event for booking: {}", booking)

        val externalEventId = booking.externalEventId
            ?: throw IllegalArgumentException("Booking must have an external event ID to be updated")

        val workspaceCalendarId = getCalendarIdByWorkspace(booking.workspace.id)
        val event = convertToGoogleEvent(booking)

        val updatedEvent = calendar.events().update(defaultCalendar, externalEventId, event).execute()

        // Check if the updated event is available in the workspace calendar
        if (!checkEventAvailability(updatedEvent, workspaceCalendarId)) {
            // If not available, revert to the previous version and throw an exception
            val previousEvent = calendar.events().get(defaultCalendar, externalEventId).execute()
            calendar.events().update(defaultCalendar, externalEventId, previousEvent).execute()
            throw WorkspaceUnavailableException("Workspace ${booking.workspace.name} is unavailable at the requested time")
        }

        return booking
    }

    override fun deleteEvent(booking: Booking) {
        logger.debug("Deleting event for booking: {}", booking)

        val externalEventId = booking.externalEventId
            ?: throw IllegalArgumentException("Booking must have an external event ID to be deleted")

        deleteEventById(externalEventId)
    }

    private fun deleteEventById(eventId: String) {
        try {
            calendar.events().delete(defaultCalendar, eventId).execute()
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode != 404 && e.statusCode != 410) {
                throw e
            }
            // If the event doesn't exist (404) or has been deleted (410), ignore the exception
            logger.warn("Event with ID {} not found or already deleted", eventId)
        }
    }

    override fun findEventsByWorkspace(workspaceId: UUID, from: Instant, to: Instant?): List<Booking> {
        logger.debug(
            "Finding events for workspace with ID {} from {} to {}",
            workspaceId,
            from,
            to ?: "infinity"
        )

        val workspaceCalendarId = getCalendarIdByWorkspace(workspaceId)
        val events = listEvents(workspaceCalendarId, from, to)

        return events.map { convertToBooking(it) }
    }

    override fun findEventsByUser(userId: UUID, from: Instant, to: Instant?): List<Booking> {
        logger.debug(
            "Finding events for user with ID {} from {} to {}",
            userId,
            from,
            to ?: "infinity"
        )

        // In a real implementation, we would need to query the user's email from a user repository
        // For simplicity, we'll assume we have a method to get the user's email
        val userEmail = getUserEmailById(userId)

        // Get all calendar IDs
        val calendarIds = calendarIdProvider.getAllCalendarIds()

        // Query all calendars for events with the user as an attendee or organizer
        val allEvents = mutableListOf<Event>()
        for (calendarId in calendarIds) {
            val events = listEvents(calendarId, from, to, userEmail)
            allEvents.addAll(events.filter { event ->
                event.organizer?.email == userEmail ||
                        event.attendees?.any { it.email == userEmail } == true
            })
        }

        return allEvents.map { convertToBooking(it) }
    }

    override fun findEventById(id: UUID): Booking? {
        logger.debug("Finding event with ID {}", id)

        // In a real implementation, we would need to map the booking ID to the external event ID
        // For simplicity, we'll assume the booking ID is the external event ID
        val externalEventId = id.toString()

        return try {
            val event = calendar.events().get(defaultCalendar, externalEventId).execute()
            convertToBooking(event)
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode == 404) {
                null
            } else {
                throw e
            }
        }
    }

    // Helper methods

    private fun getCalendarIdByWorkspace(workspaceId: UUID): String {
        return try {
            calendarIdProvider.getCalendarIdByWorkspace(workspaceId)
        } catch (e: Exception) {
            logger.warn("Failed to get calendar ID for workspace {}, using default calendar", workspaceId)
            defaultCalendar
        }
    }

    private fun listEvents(calendarId: String, from: Instant, to: Instant?, q: String? = null): List<Event> {
        val eventsRequest = calendar.events().list(calendarId)
            .setTimeMin(DateTime(from.toEpochMilli()))
            .setSingleEvents(true)

        if (to != null) {
            eventsRequest.timeMax = DateTime(to.toEpochMilli())
        }

        if (q != null) {
            eventsRequest.q = q
        }

        return eventsRequest.execute().items ?: emptyList()
    }

    private fun convertToGoogleEvent(booking: Booking): Event {
        val event = Event()
            .setSummary("Booking: ${booking.workspace.name}")
            .setDescription("Booking created by ${booking.owner.firstName} ${booking.owner.lastName}")
            .setStart(EventDateTime().setDateTime(DateTime(booking.beginBooking.toEpochMilli())))
            .setEnd(EventDateTime().setDateTime(DateTime(booking.endBooking.toEpochMilli())))

        // Add attendees
        val attendees = booking.participants.map { user ->
            EventAttendee().setEmail(user.email)
        }
        event.attendees = attendees

        // Add the owner as the organizer
        event.organizer = Event.Organizer().setEmail(booking.owner.email)

        return event
    }

    private fun convertToBooking(event: Event): Booking {
        // In a real implementation, we would need to map the Google Calendar event to a Booking
        // This would involve looking up users and workspaces by their IDs or emails
        // For simplicity, we'll create dummy objects

        val owner = createDummyUser(event.organizer?.email ?: "unknown@example.com")

        val participants = event.attendees?.map { attendee ->
            createDummyUser(attendee.email)
        } ?: emptyList()

        val workspace = createDummyWorkspace(event.summary ?: "Unknown Workspace")

        return Booking(
            id = UUID.randomUUID(), // In a real implementation, we would map this to a persistent ID
            owner = owner,
            participants = participants,
            workspace = workspace,
            beginBooking = Instant.ofEpochMilli(event.start.dateTime.value),
            endBooking = Instant.ofEpochMilli(event.end.dateTime.value),
            externalEventId = event.id
        )
    }

    private fun createDummyUser(email: String): User {
        return User(
            id = UUID.randomUUID(),
            username = email.substringBefore("@"),
            email = email,
            firstName = "Dummy",
            lastName = "User"
        )
    }

    private fun createDummyWorkspace(name: String): Workspace {
        return Workspace(
            id = UUID.randomUUID(),
            name = name,
            tag = "meeting"
        )
    }

    private fun getUserEmailById(userId: UUID): String {
        // In a real implementation, we would look up the user's email in a repository
        // For simplicity, we'll return a dummy email
        return "stanislav.radchenko@effective.band"
    }

    private fun checkEventAvailability(event: Event, workspaceCalendarId: String): Boolean {
        // In a real implementation, we would check if the workspace is available at the requested time
        // For simplicity, we'll assume it's always available
        return true
    }

    // Exception class for workspace unavailability
    class WorkspaceUnavailableException(message: String) : RuntimeException(message)
}

/**
 * Interface for providing calendar IDs for workspaces.
 */
interface CalendarIdProvider {
    fun getCalendarIdByWorkspace(workspaceId: UUID): String
    fun getAllCalendarIds(): List<String>
}
