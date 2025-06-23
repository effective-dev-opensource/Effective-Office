package band.effective.office.backend.feature.workspace.core.repository

import band.effective.office.backend.feature.workspace.core.repository.entity.CalendarIdEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for accessing calendar ID entities in the database.
 */
@Repository
interface CalendarIdRepository : JpaRepository<CalendarIdEntity, UUID> {
    /**
     * Find a calendar ID entity by workspace ID.
     *
     * @param workspaceId the workspace ID to search for
     * @return the calendar ID entity if found, null otherwise
     */
    fun findByWorkspaceId(workspaceId: UUID): CalendarIdEntity?
}