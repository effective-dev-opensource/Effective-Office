package band.effective.office.backend.feature.booking.core.domain

import band.effective.office.backend.feature.booking.core.domain.model.Booking
import java.time.Instant
import java.util.UUID

/**
 * Interface for calendar providers.
 * This interface defines the operations that calendar providers must implement.
 */
interface CalendarProvider {
    /**
     * Creates a new event in the calendar.
     *
     * @param booking The booking to create an event for
     * @return The created booking with the external event ID set
     */
    fun createEvent(booking: Booking): Booking

    /**
     * Updates an existing event in the calendar.
     *
     * @param booking The booking with updated information
     * @return The updated booking
     */
    fun updateEvent(booking: Booking): Booking

    /**
     * Deletes an event from the calendar.
     *
     * @param booking The booking to delete
     */
    fun deleteEvent(booking: Booking)

    /**
     * Finds all events for a workspace within a time range.
     *
     * @param workspaceId The ID of the workspace
     * @param from The start of the time range
     * @param to The end of the time range (optional)
     * @param returnInstances Whether to return recurring bookings as non-recurrent instances (default: true)
     * @return A list of bookings for the workspace within the time range
     */
    fun findEventsByWorkspace(workspaceId: UUID, from: Instant, to: Instant? = null, returnInstances: Boolean = true): List<Booking>

    /**
     * Finds all events for a user within a time range.
     *
     * @param userId The ID of the user
     * @param from The start of the time range
     * @param to The end of the time range (optional)
     * @param returnInstances Whether to return recurring bookings as non-recurrent instances (default: true)
     * @return A list of bookings for the user within the time range
     */
    fun findEventsByUser(userId: UUID, from: Instant, to: Instant? = null, returnInstances: Boolean = true): List<Booking>

    /**
     * Finds an event by its ID.
     *
     * @param id The ID of the booking (Google event ID)
     * @return The booking if found, null otherwise
     */
    fun findEventById(id: String): Booking?

    /**
     * Finds all events across all workspaces within a time range.
     *
     * @param from The start of the time range
     * @param to The end of the time range (optional)
     * @param returnInstances Whether to return recurring bookings as non-recurrent instances (default: true)
     * @return A list of all bookings within the time range
     */
    fun findAllEvents(from: Instant, to: Instant? = null, returnInstances: Boolean = true): List<Booking>
}
