package band.effective.office.backend.core.domain.service

import band.effective.office.backend.core.domain.model.CalendarId
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.model.WorkspaceZone
import java.util.UUID

/**
 * Service interface for workspace operations.
 * This interface defines the contract for any workspace service implementation.
 */
interface WorkspaceDomainService {
    /**
     * Retrieves a Workspace model by its id
     *
     * @param id id of requested workspace
     * @return [Workspace] with the given [id] or null if workspace with the given id doesn't exist
     */
    fun findById(id: UUID): Workspace?

    /**
     * Returns all workspaces with the given tag
     *
     * @param tag tag name of requested workspaces
     * @return List of [Workspace] with the given [tag]
     */
    fun findAllByTag(tag: String): List<Workspace>

    /**
     * Returns all workspace zones
     *
     * @return List of all [WorkspaceZone]
     */
    fun findAllZones(): List<WorkspaceZone>

    /**
     * Finds a calendar ID by workspace ID
     *
     * @param workspaceId The ID of the workspace
     * @return The calendar ID if found, null otherwise
     */
    fun findCalendarIdByWorkspaceId(workspaceId: UUID): CalendarId?

    fun findCalendarEntityById(calendarId: String): CalendarId?

    /**
     * Returns all calendar IDs
     *
     * @return List of all calendar IDs
     */
    fun findAllCalendarIds(): List<CalendarId>
}
