package band.effective.office.backend.feature.booking.calendar.google

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.service.UserDomainService
import band.effective.office.backend.core.domain.service.WorkspaceDomainService
import band.effective.office.backend.feature.booking.core.domain.CalendarProvider
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import band.effective.office.backend.feature.booking.core.exception.OverlappingBookingException
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
    private val userDomainService: UserDomainService,
    private val workspaceDomainService: WorkspaceDomainService
) : CalendarProvider {

    private val logger = LoggerFactory.getLogger(GoogleCalendarProvider::class.java)

    companion object {
        private const val RESPONSE_STATUS_DECLINED = "declined"
    }

    @Value("\${calendar.default-calendar}")
    private lateinit var defaultCalendar: String

    override fun createEvent(booking: Booking): Booking {
        logger.debug("Creating event for booking: {}", booking)

        val workspaceCalendarId = getCalendarIdByWorkspace(booking.workspace.id)
        logger.debug("workspaceCalendarId: {}", workspaceCalendarId)

        // Check if the booking overlaps with existing events before creating it
        if (!checkBookingAvailability(booking, workspaceCalendarId)) {
            throw OverlappingBookingException("Workspace ${booking.workspace.id} is unavailable at the requested time")
        }

        val event = convertToGoogleEvent(booking, workspaceCalendarId)

        val savedEvent = runCatching {
            calendar.events().insert(defaultCalendar, event).execute()
        }.onFailure {
            logger.error("Failed to create event", it)
            return@onFailure
        }.getOrNull()
        if (savedEvent == null) throw NullPointerException("Failed to create event")

        return booking.copy(id = savedEvent.id)
    }

    override fun updateEvent(booking: Booking): Booking {
        logger.debug("Updating event for booking: {}", booking)

        val eventId = booking.id

        val workspaceCalendarId = getCalendarIdByWorkspace(booking.workspace.id)

        // Check if the booking overlaps with existing events before updating it
        if (!checkBookingAvailability(booking, workspaceCalendarId)) {
            throw OverlappingBookingException("Workspace ${booking.workspace.id} is unavailable at the requested time")
        }

        val event = convertToGoogleEvent(booking, workspaceCalendarId)

        val updatedEvent = calendar.events().update(defaultCalendar, eventId, event).execute()

        return booking.copy(id = updatedEvent.id)
    }

    override fun deleteEvent(booking: Booking) {
        logger.debug("Deleting event for booking: {}", booking)
        val eventId = booking.id
        deleteEventByBooking(booking, eventId)
    }

    private fun deleteEventByBooking(booking: Booking, eventId: String) {
        try {
            calendar.events().delete(defaultCalendar, eventId).execute()
        } catch (e: GoogleJsonResponseException) {
            logger.error("Failed to delete event: {}", e.details)
            throw e
        }
    }

    override fun findEventsByWorkspace(
        workspaceId: UUID,
        from: Instant,
        to: Instant?,
        returnInstances: Boolean
    ): List<Booking> {
        logger.debug(
            "Finding events for workspace with ID {} from {} to {}, returnInstances: {}",
            workspaceId,
            from,
            to ?: "infinity",
            returnInstances
        )

        val workspaceCalendarId = getCalendarIdByWorkspace(workspaceId)
        val events = listEvents(workspaceCalendarId, from, to, returnInstances = returnInstances)

        return events.map { convertToBooking(it) }
    }

    override fun findEventsByUser(userId: UUID, from: Instant, to: Instant?, returnInstances: Boolean): List<Booking> {
        logger.debug(
            "Finding events for user with ID {} from {} to {}, returnInstances: {}",
            userId,
            from,
            to ?: "infinity",
            returnInstances
        )

        // Get the user's email from the user domain service
        val userEmail = getUserEmailById(userId)

        // Get all calendar IDs
        val calendarIds = workspaceDomainService.findAllCalendarIds().map { it.calendarId }

        // Query all calendars for events with the user as an attendee or organizer
        val bookings = mutableListOf<Booking>()
        for (calendarId in calendarIds) {
            val events = listEvents(calendarId, from, to, userEmail, returnInstances)
            val filteredEvents = events.filter { event ->
                event.organizer?.email == userEmail ||
                        event.attendees?.any { it.email == userEmail } == true
            }
            bookings.addAll(filteredEvents.map { convertToBooking(it) })
        }

        return bookings
    }

    override fun findEventById(id: String): Booking? {
        logger.debug("Finding event with ID {}", id)

        // Get all calendar IDs
        val calendarIds = workspaceDomainService.findAllCalendarIds().map { it.calendarId }

        for (calendarId in calendarIds) {
            try {
                // Try to get the event directly by ID
                val event = calendar.events().get(calendarId, id).execute()
                if (event != null) {
                    return convertToBooking(event)
                }
            } catch (e: Exception) {
                // If the event is not found in this calendar, try the next one
                logger.debug("Event with ID {} not found in calendar {}", id, calendarId)
            }
        }

        return null
    }

    override fun findAllEvents(from: Instant, to: Instant?, returnInstances: Boolean): List<Booking> {
        logger.debug(
            "Finding all events from {} to {}, returnInstances: {}",
            from,
            to ?: "infinity",
            returnInstances
        )

        // Get all calendar IDs
        val calendarIds = workspaceDomainService.findAllCalendarIds().map { it.calendarId }

        // Query all calendars for events within the time range
        val bookings = mutableListOf<Booking>()
        for (calendarId in calendarIds) {
            try {
                val events = listEvents(calendarId, from, to, returnInstances = returnInstances)
                logger.debug("findAllEvents -> events: {}", events.map { it.id.toString() })
                bookings.addAll(events.map { convertToBooking(it) })
            } catch (e: Exception) {
                logger.warn("Failed to search for events in calendar {}: {}", calendarId, e.message)
            }
        }

        return bookings
    }

    // Helper methods

    private fun getCalendarIdByWorkspace(workspaceId: UUID): String {
        return try {
            val calendarId = workspaceDomainService.findCalendarIdByWorkspaceId(workspaceId)
            calendarId?.calendarId ?: defaultCalendar
        } catch (e: Exception) {
            logger.warn("Failed to get calendar ID for workspace {}, using default calendar", workspaceId)
            defaultCalendar
        }
    }

    private fun listEvents(
        calendarId: String,
        from: Instant,
        to: Instant?,
        q: String? = null,
        returnInstances: Boolean = true
    ): List<Event> {
        val eventsRequest = calendar.events().list(calendarId)
            .setTimeMin(DateTime(from.toEpochMilli()))
            .setSingleEvents(returnInstances)

        if (to != null) {
            eventsRequest.timeMax = DateTime(to.toEpochMilli())
        }

        if (q != null) {
            eventsRequest.q = q
        }

        return try {
            val events = eventsRequest.execute().items ?: emptyList()

            // Filter out events where a resource attendee has declined the meeting
            events.filter { event ->
                // Check if any resource attendee has declined
                val resourceDeclined = event.attendees?.any { attendee ->
                    attendee.resource == true && attendee.responseStatus == RESPONSE_STATUS_DECLINED
                } ?: false

                // Keep events where no resource attendee has declined
                !resourceDeclined
            }
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

    private fun convertToGoogleEvent(booking: Booking, workspaceCalendarId: String? = null): Event {
        val ownerEmail = booking.owner?.email ?: defaultCalendar
        val event = Event()
            .setSummary("Meet${booking.owner?.let { " ${it.firstName} ${it.lastName}" }.orEmpty()}")
            .setDescription(
                "$ownerEmail - почта организатора"
            )
            .setStart(createEventDateTime(booking.beginBooking.toEpochMilli()))
            .setEnd(createEventDateTime(booking.endBooking.toEpochMilli()))

        // Add recurrence if present
        booking.recurrence?.let { recurrence ->
            event.recurrence = RecurrenceRuleConverter.toGoogleRecurrenceRule(recurrence)
        }

        // Add attendees
        val attendees = booking.participants.map { user ->
            EventAttendee().setEmail(user.email)
        }.toMutableList()

        // Add the owner as the organizer
        event.organizer = Event.Organizer().setEmail(ownerEmail)

        // Add workspace as an attendee if workspaceCalendarId is provided
        workspaceCalendarId?.let {
            val workspaceAttendee = EventAttendee()
                .setEmail(it)
                .setResource(true)
            attendees.add(workspaceAttendee)
        }

        event.attendees = attendees

        return event
    }

    /**
     * Retrieves the calendar ID of the workspace from the event.
     * If the ID is not found, returns a default value with a warning log.
     *
     * @param event The event from which to retrieve the calendar ID.
     * @return Calendar ID of the workspace or default value.
     */
    private fun getCalendarId(event: Event): String? {
        val calendarId = event.attendees
            ?.firstOrNull { it?.resource == true }
            ?.email

        if (calendarId == null) {
            logger.warn("No resource attendee found in event with ID: ${event.id}. Using provided calendar ID as fallback.")
        }

        return calendarId
    }

    private fun convertToBooking(event: Event): Booking {
        // Get the organizer's email and find the corresponding user
        val organizer = event?.organizer?.email

        // Check if the user found by organizer email is a system user
        val user = organizer?.let { userDomainService.findByEmail(it) }
        val email = if (user != null && user.tag == "system") {
            logger.trace("[toBookingDTO] organizer email derived from event description")
            event.description?.substringBefore(" ") ?: ""
        } else {
            logger.trace("[toBookingDTO] organizer email derived from event.organizer field")
            organizer
        }

        val owner = email?.let { findOrCreateUserByEmail(email) }

        // Get the attendees' emails and find the corresponding users
        val participants = event.attendees?.mapNotNull { attendee ->
            findOrCreateUserByEmail(attendee.email)
        } ?: emptyList()

        // Get the calendar ID from the event or use the provided one
        val workspaceCalendarId = getCalendarId(event)

        val workspace: Workspace = try {
            if (workspaceCalendarId != null) {
                val calendarEntity = workspaceDomainService.findCalendarEntityById(workspaceCalendarId)
                if (calendarEntity == null) throw IllegalStateException("CalendarEntity not found for calendar ID: $workspaceCalendarId")
                val foundWorkspace = workspaceDomainService.findById(calendarEntity.workspaceId)
                foundWorkspace
                    ?: throw IllegalStateException("Workspace not found for ID: ${calendarEntity.workspaceId}")
            } else {
                throw IllegalStateException("Workspace not found for calendar ID: $workspaceCalendarId")
            }
        } catch (e: IllegalStateException) {
            logger.error("Workspace not found for calendar ID: $workspaceCalendarId")
            Workspace(UUID.randomUUID(), "Unknown", "Unknown", emptyList())
        }


        // Extract recurring booking ID from event description if it exists
        val recurringBookingIdStr = event.description?.let {
            val regex = "Recurring Booking ID: ([0-9a-f-]+)".toRegex()
            val matchResult = regex.find(it)
            matchResult?.groupValues?.get(1)
        }

        // Determine if the booking is editable based on the organizer
        // If the organizer is the defaultCalendar, then the booking is editable
        // Otherwise, it's not editable (created from Google Calendar)
        val isEditable = organizer == defaultCalendar

        return Booking(
            id = event.id,
            owner = owner,
            participants = participants,
            workspace = workspace,
            beginBooking = Instant.ofEpochMilli(event.start.dateTime.value),
            endBooking = Instant.ofEpochMilli(event.end.dateTime.value),
            recurrence = RecurrenceRuleConverter.fromGoogleRecurrenceRule(event.recurrence),
            recurringBookingId = recurringBookingIdStr,
            isEditable = isEditable
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

        // If not found, create a system user
        return User(
            id = UUID.randomUUID(),
            username = email.substringBefore("@"),
            email = email,
            firstName = "Service",
            lastName = "Account",
            tag = "system" // TODO to enum
        )
    }

    private fun getUserEmailById(userId: UUID): String {
        // Look up the user's email in the user domain service
        val user = userDomainService.findById(userId)
        return user?.email ?: throw IllegalArgumentException("User with ID $userId not found")
    }

    /**
     * Checks if a booking overlaps with existing events in the workspace calendar.
     *
     * @param booking The booking to check availability for
     * @param workspaceCalendarId The calendar ID of the workspace
     * @return True if the booking doesn't overlap with existing events, false otherwise
     */
    private fun checkBookingAvailability(booking: Booking, workspaceCalendarId: String): Boolean {
        val startTime = booking.beginBooking
        val endTime = booking.endBooking

        val events = listEvents(workspaceCalendarId, startTime, endTime)

        if (events.isEmpty()) return true

        return events.none { existingEvent ->
            if (booking.id == existingEvent.id) return@none false
            val existingStart = Instant.ofEpochMilli(existingEvent.start.dateTime.value)
            val existingEnd = Instant.ofEpochMilli(existingEvent.end.dateTime.value)
            val isOverlapping = startTime < existingEnd && existingStart < endTime
            logger.debug("Overlapping matches: $isOverlapping | $startTime < $existingEnd && $existingStart < $endTime")
            isOverlapping
        }
    }

    /**
     * Creates an EventDateTime object with proper time zone information.
     *
     * @param timestamp The timestamp in milliseconds
     * @return An EventDateTime object with the timestamp and time zone set
     */
    private fun createEventDateTime(timestamp: Long): EventDateTime {
        return EventDateTime().apply {
            dateTime = DateTime(timestamp)
            timeZone = java.util.TimeZone.getDefault().id
        }
    }
}

/**
 * Interface for providing calendar IDs for workspaces.
 */
interface CalendarIdProvider {
    fun getCalendarIdByWorkspace(workspaceId: UUID): String
    fun getAllCalendarIds(): List<String>
}
