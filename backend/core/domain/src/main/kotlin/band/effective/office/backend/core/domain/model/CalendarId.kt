package band.effective.office.backend.core.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a calendar ID linked to a workspace.
 *
 * @property id The unique identifier for the calendar ID
 * @property workspaceId The ID of the workspace this calendar ID is linked to
 * @property calendarId The Google Calendar ID
 * @property createdAt When this calendar ID was created
 * @property updatedAt When this calendar ID was last updated
 */
data class CalendarId(
    val id: UUID,
    val workspaceId: UUID,
    val calendarId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)