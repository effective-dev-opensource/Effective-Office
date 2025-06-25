package band.effective.office.backend.feature.workspace.core.repository.mapper

import band.effective.office.backend.core.domain.model.CalendarId
import band.effective.office.backend.feature.workspace.core.repository.entity.CalendarIdEntity

/**
 * Mapper for converting between CalendarIdEntity and CalendarId domain model.
 */
object CalendarIdMapper {
    /**
     * Convert a CalendarIdEntity to a CalendarId domain model.
     *
     * @param entity The entity to convert
     * @return The domain model
     */
    fun toDomain(entity: CalendarIdEntity): CalendarId = CalendarId(
        id = entity.id,
        workspaceId = entity.workspace.id,
        calendarId = entity.calendarId,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
}