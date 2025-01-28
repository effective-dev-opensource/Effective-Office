package office.effective.features.booking.repository

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import office.effective.common.constants.BookingConstants
import office.effective.common.exception.InstanceNotFoundException
import office.effective.common.exception.MissingIdException
import office.effective.common.exception.WorkspaceUnavailableException
import office.effective.features.calendar.repository.CalendarIdsRepository
import office.effective.features.booking.converters.GoogleCalendarConverter
import office.effective.model.Booking
import office.effective.features.user.repository.UserEntity
import office.effective.features.user.repository.cache.UsersCache
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/**
 * Class that executes Google calendar queries for booking meeting rooms
 *
 * Filters out all events that have a start less than the calendar.minTime from application.conf
 */
class BookingCalendarRepository(
    private val calendarIdsRepository: CalendarIdsRepository,
    private val userCache: UsersCache,
    private val calendar: Calendar,
    private val googleCalendarConverter: GoogleCalendarConverter
) : IBookingRepository {
    private val calendarEvents = calendar.Events()
    private val defaultCalendar = BookingConstants.DEFAULT_CALENDAR
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Finds workspace calendar id by workspace id
     *
     * @param workspaceId
     * @return calendar id by workspace id in database
     */
    private fun getCalendarIdByWorkspace(workspaceId: UUID): String {
        return try {
            calendarIdsRepository.findByWorkspace(workspaceId)
        } catch (e: InstanceNotFoundException) {
            defaultCalendar
        }
    }

    /**
     * Returns whether a booking with the given id exists
     *
     * @param id booking id
     * @return true if booking exists
     */
    override fun existsById(id: String): Boolean {
        logger.debug("[existsById] checking whether a booking with id={} exists", id)
        val event: Any?
        event = findByCalendarIdAndBookingId(id)
        return event != null
    }

    /**
     * Deletes the booking with the given id
     *
     * @param id booking id
     */
    override fun deleteById(id: String) {
        logger.debug("[deleteById] deleting the booking with id={}", id)
        try {
            calendarEvents.delete(defaultCalendar, id).execute() //We can't delete directly from workspace calendar
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode != 404 && e.statusCode != 410) {
                throw e
            }
        }
    }

    /**
     * Retrieves a booking model by its id.
     * Retrieved booking contains user and workspace models without integrations and utilities
     *
     * @param bookingId booking id
     * @return [Booking] with the given [bookingId] or null if booking with the given id doesn't exist
     */
    override fun findById(bookingId: String): Booking? {
        logger.debug("[findById] retrieving a booking with id={}", bookingId)
        val event: Event? = findByCalendarIdAndBookingId(bookingId)
        return event?.let { googleCalendarConverter.toBookingModel(it) }
    }

    /**
     * Retrieves an event by calendar id and booking id.
     * Retrieved booking contains user and workspace models without integrations and utilities
     *
     * @param bookingId booking id
     * @param calendarId the calendar in which to search for the event
     * @return [Event] with the given [bookingId] from calendar with [calendarId]
     * or null if event with the given id doesn't exist
     */
    private fun findByCalendarIdAndBookingId(bookingId: String, calendarId: String = defaultCalendar): Event? {
        logger.trace("Retrieving event from {} calendar by id", calendarId)
        return try {
            calendar.events().get(calendarId, bookingId).execute()
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode == 404) return null
            else throw e
        }
    }

    /**
     * Request template containing all required parameters
     *
     * @param timeMin lover bound for filtering bookings by start time.
     * Old Google calendar events may not appear correctly in the system and cause unexpected exceptions
     * @param timeMax
     * @param singleEvents
     * @param calendarId
     */
    private fun basicQuery(
        timeMin: Long,
        timeMax: Long? = null,
        singleEvents: Boolean = true,
        calendarId: String = defaultCalendar
    ): Calendar.Events.List {
        return calendarEvents.list(calendarId)
            .setSingleEvents(singleEvents)
            .setTimeMin(DateTime(timeMin))
            .setTimeMax(timeMax?.let { DateTime(it) })
    }

    /**
     * Returns all bookings with the given workspace id
     *
     * @param workspaceId
     * @param eventRangeFrom lower bound (exclusive) for a endBooking to filter by.
     * Old Google calendar events may not appear correctly in the system and cause unexpected exceptions
     * @param eventRangeTo upper bound (exclusive) for a beginBooking to filter by. Optional.
     * @return List of all workspace [Booking]
     */
    override fun findAllByWorkspaceId(workspaceId: UUID, eventRangeFrom: Long, eventRangeTo: Long?): List<Booking> {
        logger.debug(
            "[findAllByWorkspaceId] retrieving all bookings for workspace with id={} in range from {} to {}",
            workspaceId,
            Instant.ofEpochMilli(eventRangeFrom),
            eventRangeTo?.let { Instant.ofEpochMilli(it) } ?: "infinity"
        )
        val workspaceCalendarId = getCalendarIdByWorkspace(workspaceId)
        val getSingleEvents = true
        val eventsWithWorkspace = basicQuery(eventRangeFrom, eventRangeTo, getSingleEvents, workspaceCalendarId)
            .execute().items

        return eventsWithWorkspace.toList().map { googleCalendarConverter.toBookingModel(it) }
    }

    /**
     * Checks whether the given event organised by user with the given email.
     * Organiser email may be specified at [Event.description] or [Event.organizer].email
     *
     * @param event
     * @param email
     * @return List of all user [Booking]
     * @author Danil Kiselev, Daniil Zavyalov
     */
    private fun checkEventOrganizer(event: Event, email: String): Boolean {
        if (event.organizer?.email == defaultCalendar) {
            return event.description.contains(email)
        }
        //TODO: if event was created by defaultCalendar account, but not from Effective Office, this method will return false
        return event.organizer?.email == email
    }

    /**
     * Retrieves user email from database by user id
     *
     * @param id
     * @return user email
     * @throws InstanceNotFoundException if user with the given id doesn't exist in database
     * @author Danil Kiselev
     */
    private fun findUserEmailByUserId(id: UUID): String {
        return userCache.getById(id)?.email ?: throw InstanceNotFoundException(
            UserEntity::class, "User with id $id not found"
        )
    }

    private fun getEventsWithQParam(
        calendarIds: List<String>,
        q: String,
        eventRangeFrom: Long,
        eventRangeTo: Long?
    ): List<Event> {
        val executor = Executors.newFixedThreadPool(calendarIds.size)
        val futures = calendarIds.map { calendarId ->
            CompletableFuture.supplyAsync {
                basicQuery(
                    timeMin = eventRangeFrom,
                    timeMax = eventRangeTo,
                    singleEvents = true,
                    calendarId = calendarId
                ).setQ(q)
                    .execute().items
            }
        }
        val allEvents = CompletableFuture.allOf(*futures.toTypedArray())
            .thenApply {
                futures.map { it.get() }
            }
            .join()
        executor.shutdown()

        return allEvents.flatten()
    }

    private fun getAllEvents(
        calendarIds: List<String>,
        eventRangeFrom: Long,
        eventRangeTo: Long?
    ): List<Event> {
        val executor = Executors.newFixedThreadPool(calendarIds.size)
        val futures = calendarIds.map { calendarId ->
            CompletableFuture.supplyAsync {
                basicQuery(
                    timeMin = eventRangeFrom,
                    timeMax = eventRangeTo,
                    singleEvents = true,
                    calendarId = calendarId
                ).execute().items
            }
        }
        val allEvents = CompletableFuture.allOf(*futures.toTypedArray())
            .thenApply {
                futures.map { it.get() }
            }
            .join()
        executor.shutdown()

        return allEvents.flatten()
    }

    /**
     * Returns all bookings with the given owner id
     *
     * @param ownerId
     * @param eventRangeFrom lower bound (exclusive) for a endBooking to filter by.
     * Old Google calendar events may not appear correctly in the system and cause unexpected exceptions
     * @param eventRangeTo upper bound (exclusive) for a beginBooking to filter by. Optional.
     * @return List of all user [Booking]
     * @throws InstanceNotFoundException if user with the given id doesn't exist in database
     * @author Daniil Zavyalov, Danil Kiselev
     */
    override fun findAllByOwnerId(ownerId: UUID, eventRangeFrom: Long, eventRangeTo: Long?): List<Booking> {
        logger.debug(
            "[findAllByOwnerId] retrieving all bookings for user with id={} in range from {} to {}",
            ownerId,
            Instant.ofEpochMilli(eventRangeFrom),
            eventRangeTo?.let { Instant.ofEpochMilli(it) } ?: "infinity"
        )
        val userEmail: String = findUserEmailByUserId(ownerId)
        val calendars: List<String> = calendarIdsRepository.findAllCalendarsId()

        val eventsWithUser = getEventsWithQParam(calendars, userEmail, eventRangeFrom, eventRangeTo)

        val result = mutableListOf<Booking>()
        for (event in eventsWithUser) {
            if (checkEventOrganizer(event, userEmail)) {
                result.add(googleCalendarConverter.toBookingModel(event))
            } else {
                logger.trace("[findAllByOwnerId] filtered out event: {}", event)
            }
        }
        return result
    }

    /**
     * Returns all bookings with the given workspace and owner id
     *
     * @param ownerId
     * @param workspaceId
     * @param eventRangeFrom lower bound (exclusive) for a endBooking to filter by.
     * Old Google calendar events may not appear correctly in the system and cause unexpected exceptions
     * @param eventRangeTo upper bound (exclusive) for a beginBooking to filter by. Optional.
     * @return List of all [Booking]s with the given workspace and owner id
     */
    override fun findAllByOwnerAndWorkspaceId(
        ownerId: UUID,
        workspaceId: UUID,
        eventRangeFrom: Long,
        eventRangeTo: Long?
    ): List<Booking> {
        logger.debug(
            "[findAllByOwnerAndWorkspaceId] retrieving all bookings for a workspace with id={} created by user with id={} in range from {} to {}",
            workspaceId,
            ownerId,
            Instant.ofEpochMilli(eventRangeFrom),
            eventRangeTo?.let { Instant.ofEpochMilli(it) } ?: "infinity"
        )
        val userEmail: String = findUserEmailByUserId(ownerId)
        val workspaceCalendarId = getCalendarIdByWorkspace(workspaceId)

        val eventsWithUserAndWorkspace = basicQuery(eventRangeFrom, eventRangeTo, true, workspaceCalendarId)
            .setQ(userEmail)
            .execute().items

        val result = mutableListOf<Booking>()
        for (event in eventsWithUserAndWorkspace) {
            if (checkEventOrganizer(event, userEmail)) {
                result.add(googleCalendarConverter.toBookingModel(event))
            }  else {
                logger.trace("[findAllByOwnerAndWorkspaceId] filtered out event: {}", event)
            }
        }

        return result
    }

    /**
     * Retrieves all bookings
     *
     * @param eventRangeFrom lower bound (exclusive) for a endBooking to filter by.
     * Old Google calendar events may not appear correctly in the system and cause unexpected exceptions
     * @param eventRangeTo upper bound (exclusive) for a beginBooking to filter by. Optional.
     * @return All [Booking]s
     */
    override fun findAll(eventRangeFrom: Long, eventRangeTo: Long?): List<Booking> {
        logger.debug(
            "[findAll] retrieving all bookings in range from {} to {}",
            Instant.ofEpochMilli(eventRangeFrom),
            eventRangeTo?.let { Instant.ofEpochMilli(it) } ?: "infinity"
        )
        val calendars: List<String> = calendarIdsRepository.findAllCalendarsId()
        val events: List<Event> = getAllEvents(calendars, eventRangeFrom, eventRangeTo)
        return events.map { googleCalendarConverter.toBookingModel(it) }
    }

    /**
     * Saves a given booking. If given model will have an id, it will be ignored.
     * Use the returned model for further operations
     *
     * @param booking [Booking] to be saved
     * @return saved [Booking]
     */
    override fun save(booking: Booking): Booking {
        logger.debug("[save] saving booking of workspace with id {}", booking.workspace.id)
        logger.trace("[save] booking to save: {}", booking)
        val workspaceCalendar = calendarIdsRepository.findByWorkspace(
            booking.workspace.id ?: throw MissingIdException("Missing workspace id")
        )
        val event = googleCalendarConverter.toGoogleEvent(booking)

        val savedEvent = calendar.Events().insert(defaultCalendar, event).execute()
        if (checkBookingAvailable(savedEvent, workspaceCalendar)) {
            val savedBooking = googleCalendarConverter.toBookingModel(savedEvent)
            logger.trace("[save] saved booking: {}", savedBooking)
            return savedBooking
        } else {
            deleteById(savedEvent.id)
            throw WorkspaceUnavailableException("Workspace ${booking.workspace.name} " +
                    "unavailable at time between ${booking.beginBooking} and ${booking.endBooking}")
        }

    }

    /**
     * Updates a given booking. Use the returned model for further operations
     *
     * @param booking changed booking
     * @return [Booking] after change saving
     * @throws MissingIdException if [Booking.id] or [Booking.workspace].id is null
     * @throws InstanceNotFoundException if booking given id doesn't exist in the database
     * @throws WorkspaceUnavailableException if booking unavailable because of collision check
     */
    override fun update(booking: Booking): Booking {
        logger.debug("[update] updating booking of workspace with id {}", booking.id)
        logger.trace("[update] new booking: {}", booking)
        val workspaceCalendar = calendarIdsRepository.findByWorkspace(
            booking.workspace.id ?: throw MissingIdException("Missing workspace id")
        )
        val bookingId = booking.id ?: throw MissingIdException("Update model must have id")
        val previousVersionOfEvent = findByCalendarIdAndBookingId(bookingId) ?: throw InstanceNotFoundException(
            WorkspaceBookingEntity::class, "Booking with id $bookingId not wound"
        )
        logger.trace("[update] previous version of event: {}", previousVersionOfEvent)
        val eventOnUpdate = googleCalendarConverter.toGoogleEvent(booking)

        val updatedEvent: Event = calendarEvents.update(defaultCalendar, bookingId, eventOnUpdate).execute()

        val sequence = updatedEvent.sequence
        if (checkBookingAvailable(updatedEvent, workspaceCalendar)) {
            val updatedBooking = googleCalendarConverter.toBookingModel(updatedEvent)
            logger.trace("[update] updated booking: {}", updatedBooking)
            return updatedBooking
        } else {
            previousVersionOfEvent.sequence = sequence
            calendarEvents.update(defaultCalendar, bookingId, previousVersionOfEvent).execute()
            throw WorkspaceUnavailableException("Workspace ${booking.workspace.name} " +
                    "unavailable at time between ${booking.beginBooking} and ${booking.endBooking}")
        }
    }

    /**
     * Launch checkSingleEventCollision for non-cycle event
     * or receive instances for recurrent event and checks all instances.
     *
     * @param incomingEvent: [Event] - Must take only SAVED event
     * @return Boolean. True if booking available
     * */
    private fun checkBookingAvailable(incomingEvent: Event, workspaceCalendar: String): Boolean {
        logger.debug(
            "[checkBookingAvailable] checking if workspace with calendar id={} available for event {}",
            workspaceCalendar,
            incomingEvent
        )
        var isAvailable = false;

        if (incomingEvent.recurrence != null) {
            //TODO: Check, if we can receive instances without pushing this event into calendar
            calendarEvents.instances(workspaceCalendar, incomingEvent.id).setMaxResults(50).execute().items.forEach { event ->
                if (!checkSingleEventCollision(event, workspaceCalendar)) {
                    return@checkBookingAvailable false
                } else {
                    isAvailable = true
                }
            }
        } else {
            isAvailable = checkSingleEventCollision(incomingEvent, workspaceCalendar)
        }
        logger.debug("[checkBookingAvailable] result {}", true)
        return isAvailable
    }

    /**
     * Contains collision condition. Checks collision between single event from param and all saved events from event.start (leftBorder) until event.end (rightBorder)
     *
     * @param event: [Event] - Must take only SAVED event
     * */
    private fun checkSingleEventCollision(event: Event, workspaceCalendar: String): Boolean {
        val savedEvents = basicQuery(event.start.dateTime.value, event.end.dateTime.value, true, workspaceCalendar)
            .execute().items
        for (i in savedEvents) {
            if (
                !((i.start.dateTime.value >= event.end.dateTime.value) || (i.end.dateTime.value <= event.start.dateTime.value))
                && (i.id != event.id)
            ) {
                return false
            }
        }

        return true
    }
}