package band.effective.office.backend.feature.booking.calendar.google

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * A simple implementation of the CalendarIdProvider interface.
 * This implementation stores calendar IDs in memory.
 * In a real application, this would be replaced with a repository that fetches calendar IDs from a database.
 */
@Component
class SimpleCalendarIdProvider : CalendarIdProvider {

    @Value("\${calendar.default-calendar}")
    private lateinit var defaultCalendar: String
    private val calendarIds = ConcurrentHashMap<UUID, String>()

    init {
        // Initialize with some dummy data for testing
        // In a real application, this would be loaded from a database
        calendarIds[UUID.fromString("00000000-0000-0000-0000-000000000001")] = "workplace1@example.com"
    }

    override fun getCalendarIdByWorkspace(workspaceId: UUID): String {
        return calendarIds[workspaceId] ?: defaultCalendar
    }

    override fun getAllCalendarIds(): List<String> {
        return calendarIds.values.toList() + defaultCalendar
    }
}
