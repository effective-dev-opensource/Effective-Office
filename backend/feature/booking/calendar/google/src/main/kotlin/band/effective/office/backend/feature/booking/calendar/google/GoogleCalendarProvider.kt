package band.effective.office.backend.feature.booking.calendar.google

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.service.UserDomainService
import band.effective.office.backend.core.domain.service.WorkspaceDomainService
import band.effective.office.backend.feature.booking.core.domain.CalendarProvider
import band.effective.office.backend.feature.booking.core.domain.model.Booking
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
    private val calendarIdProvider: CalendarIdProvider,
    private val userDomainService: UserDomainService,
    private val workspaceDomainService: WorkspaceDomainService
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
            calendar.events().insert(workspaceCalendarId, event).execute()
        }.onFailure {
            logger.error("Failed to create event", it)
            return@onFailure
        }.getOrNull()
        if (savedEvent == null) throw NullPointerException("Failed to create event")

        logger.debug("savedEvent: {}", savedEvent)

        // Check if the event was successfully created in the workspace calendar
        if (!checkEventAvailability(savedEvent, workspaceCalendarId)) {
            // If not available, delete the event and throw an exception
            deleteEventByBooking(booking)
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

        booking.externalEventId ?: throw IllegalArgumentException("Booking must have an external event ID to be deleted")

        deleteEventByBooking(booking)
    }

    private fun deleteEventByBooking(booking: Booking) {
        try {
            val calendarId = getCalendarIdByWorkspace(booking.workspace.id)
            calendar.events().delete(calendarId, booking.externalEventId).execute()
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode != 404 && e.statusCode != 410) {
                throw e
            }
            // If the event doesn't exist (404) or has been deleted (410), ignore the exception
            logger.warn("Event with ID {} not found or already deleted", booking.externalEventId)
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

        return events.map { convertToBooking(it, workspaceCalendarId) }
    }

    override fun findEventsByUser(userId: UUID, from: Instant, to: Instant?): List<Booking> {
        logger.debug(
            "Finding events for user with ID {} from {} to {}",
            userId,
            from,
            to ?: "infinity"
        )

        // Get the user's email from the user domain service
        val userEmail = getUserEmailById(userId)

        // Get all calendar IDs
        val calendarIds = calendarIdProvider.getAllCalendarIds()

        // Query all calendars for events with the user as an attendee or organizer
        val bookings = mutableListOf<Booking>()
        for (calendarId in calendarIds) {
            val events = listEvents(calendarId, from, to, userEmail)
            val filteredEvents = events.filter { event ->
                event.organizer?.email == userEmail ||
                        event.attendees?.any { it.email == userEmail } == true
            }
            bookings.addAll(filteredEvents.map { convertToBooking(it, calendarId) })
        }

        return bookings
    }

    override fun findEventById(id: UUID): Booking? {
        logger.debug("Finding event with ID {}", id)

        // Search for events with the booking ID in the description
        // We need to search in all calendars because we don't know which calendar the event is in
        val calendarIds = calendarIdProvider.getAllCalendarIds()

        // Search for events with the booking ID in the description
        // We'll search for events in the last year to limit the search
        val oneYearAgo = Instant.now().minusSeconds(365 * 24 * 60 * 60) // TODO

        for (calendarId in calendarIds) {
            try {
                // Search for events with the booking ID in the description
                val events = listEvents(calendarId, oneYearAgo, null)

                // Find the event with the exact booking ID
                val event = events.firstOrNull { event ->
                    event.description?.contains(id.toString()) == true
                }

                if (event != null) {
                    return convertToBooking(event, calendarId)
                }
            } catch (e: Exception) {
                logger.warn("Failed to search for events in calendar {}: {}", calendarId, e.message)
            }
        }

        return null
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

        return try {
            eventsRequest.execute().items ?: emptyList()
        } catch (e: GoogleJsonResponseException) {
            logger.error("Failed to list events from Google Calendar: {}", e.details)
            if (e.statusCode == 404) {
                logger.warn("Calendar with ID {} not found", calendarId)
            } else if (e.statusCode == 403) {
                logger.warn("Permission denied for calendar with ID {}", calendarId)
            }
            emptyList()
        } catch (e: Exception) {
            logger.error("Unexpected error when listing events from Google Calendar", e)
            emptyList()
        }
    }

    private fun convertToGoogleEvent(booking: Booking): Event {
        val event = Event()
            .setSummary("Booking: ${booking.workspace.tag} - ${booking.owner.firstName} ${booking.owner.lastName}")
            .setDescription("Booking created by ${booking.owner.firstName} ${booking.owner.lastName}\nBooking ID: ${booking.id}")
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

    private fun convertToBooking(event: Event, calendarId: String? = null): Booking {
        // Get the organizer's email and find the corresponding user
        logger.debug("event.organizer?.email: ${event.organizer?.email}")
        val organizerEmail = event.organizer.email ?: "unknown@example.com"
        val owner = findOrCreateUserByEmail(organizerEmail)

        // Get the attendees' emails and find the corresponding users
        val participants = event.attendees?.mapNotNull { attendee ->
            findOrCreateUserByEmail(attendee.email)
        } ?: emptyList()

        // Use the calendar ID (which is the workspace name) to find the workspace
        // If calendarId is not provided, fall back to extracting from event summary
        val workspaceName = calendarId ?: event.summary?.substringAfter("Booking: ") ?: "Unknown Workspace"

        // Try to find a workspace with this name
        // In a real implementation, we might have a more robust way to map events to workspaces
        val workspace = workspaceDomainService.findAllByTag("meeting")
            .firstOrNull { it.name == workspaceName }
            ?: throw IllegalStateException("Workspace with name $workspaceName not found")

        // Extract booking ID from event description or use a random UUID if not found
        val bookingIdStr = event.description?.let {
            val regex = "Booking ID: ([0-9a-f-]+)".toRegex()
            val matchResult = regex.find(it)
            matchResult?.groupValues?.get(1)
        }

        val bookingId = bookingIdStr?.let { UUID.fromString(it) } ?: UUID.randomUUID()

        return Booking(
            id = bookingId,
            owner = owner,
            participants = participants,
            workspace = workspace,
            beginBooking = Instant.ofEpochMilli(event.start.dateTime.value),
            endBooking = Instant.ofEpochMilli(event.end.dateTime.value),
            externalEventId = event.id
        )
    }

    /**
     * Finds a user by email or creates a new one if not found.
     *
     * @param email The email of the user to find or create
     * @return The found or created user
     */
    private fun findOrCreateUserByEmail(email: String): User {
        // Try to find a user with this email
        val user = userDomainService.findByEmail(email)

        if (user != null) {
            return user
        }

        // If not found, create a new user
        val newUser = User(
            id = UUID.randomUUID(),
            username = email.substringBefore("@"),
            email = email,
            firstName = "Unknown",
            lastName = "User"
        )

        // Save the new user
        return userDomainService.createUser(newUser)
    }

    private fun getUserEmailById(userId: UUID): String {
        // Look up the user's email in the user domain service
        val user = userDomainService.findById(userId)
        return user?.email ?: throw IllegalArgumentException("User with ID $userId not found")
    }

    /**
     * Checks if a workspace is available at the requested time.
     *
     * @param event The event to check availability for
     * @param workspaceCalendarId The calendar ID of the workspace
     * @return True if the workspace is available, false otherwise
     */
    private fun checkEventAvailability(event: Event, workspaceCalendarId: String): Boolean {

        // Get the start and end time of the event
        val startTime = Instant.ofEpochMilli(event.start.dateTime.value)
        val endTime = Instant.ofEpochMilli(event.end.dateTime.value)

        // Get all events in the workspace calendar during this time period
        val events = listEvents(workspaceCalendarId, startTime, endTime)

        // Check if there are any overlapping events
        // Exclude the event itself if it's already in the calendar
        val overlappingEvents = events.filter { existingEvent ->
            // Skip the event itself
            if (existingEvent.id == event.id) {
                return@filter false
            }

            // Check if the events overlap
            val existingStartTime = Instant.ofEpochMilli(existingEvent.start.dateTime.value)
            val existingEndTime = Instant.ofEpochMilli(existingEvent.end.dateTime.value)

            // Events overlap if one starts before the other ends and ends after the other starts
            (startTime.isBefore(existingEndTime) && endTime.isAfter(existingStartTime))
        }

        // The workspace is available if there are no overlapping events
        return overlappingEvents.isEmpty()
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
