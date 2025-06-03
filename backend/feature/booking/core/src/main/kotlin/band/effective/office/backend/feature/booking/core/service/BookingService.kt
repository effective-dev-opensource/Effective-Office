package band.effective.office.backend.feature.booking.core.service

import band.effective.office.backend.feature.booking.core.domain.CalendarProvider
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service

/**
 * Service for managing bookings.
 * This service uses a CalendarProvider to interact with the calendar.
 */
@Service
class BookingService(
    private val calendarProvider: CalendarProvider,
) {

    /**
     * Creates a new booking.
     *
     * @param booking The booking to create
     * @return The created booking with the external event ID set
     */
    fun createBooking(booking: Booking): Booking {
        return calendarProvider.createEvent(booking)
    }

    /**
     * Updates an existing booking.
     *
     * @param booking The booking with updated information
     * @return The updated booking
     */
    fun updateBooking(booking: Booking): Booking {
        return calendarProvider.updateEvent(booking)
    }

    /**
     * Deletes a booking.
     *
     * @param booking The booking to delete
     */
    fun deleteBooking(booking: Booking) {
        calendarProvider.deleteEvent(booking)
    }

    /**
     * Deletes a booking by its ID.
     *
     * @param id The ID of the booking to delete
     * @return true if the booking was deleted, false if it wasn't found
     */
    fun deleteBookingById(id: UUID): Boolean {
        val booking = getBookingById(id) ?: return false
        calendarProvider.deleteEvent(booking)
        return true
    }

    /**
     * Gets a booking by its ID.
     *
     * @param id The ID of the booking
     * @return The booking if found, null otherwise
     */
    fun getBookingById(id: UUID): Booking? {
        return calendarProvider.findEventById(id)
    }

    /**
     * Gets all bookings for a workspace within a time range.
     *
     * @param workspaceId The ID of the workspace
     * @param from The start of the time range
     * @param to The end of the time range (optional)
     * @return A list of bookings for the workspace within the time range
     */
    fun getBookingsByWorkspace(workspaceId: UUID, from: Instant, to: Instant? = null): List<Booking> {
        return calendarProvider.findEventsByWorkspace(workspaceId, from, to)
    }

    /**
     * Gets all bookings for a user within a time range.
     *
     * @param userId The ID of the user
     * @param from The start of the time range
     * @param to The end of the time range (optional)
     * @return A list of bookings for the user within the time range
     */
    fun getBookingsByUser(userId: UUID, from: Instant, to: Instant? = null): List<Booking> {
        return calendarProvider.findEventsByUser(userId, from, to)
    }

    /**
     * Gets all bookings for a user and workspace within a time range.
     *
     * @param userId The ID of the user
     * @param workspaceId The ID of the workspace
     * @param from The start of the time range
     * @param to The end of the time range (optional)
     * @return A list of bookings for the user and workspace within the time range
     */
    fun getBookingsByUserAndWorkspace(
        userId: UUID,
        workspaceId: UUID,
        from: Instant,
        to: Instant? = null
    ): List<Booking> {
        return calendarProvider.findEventsByUser(userId, from, to)
            .filter { it.workspace.id == workspaceId }
    }
}
