package band.effective.office.backend.feature.workspace.core.repository

import band.effective.office.backend.feature.workspace.core.domain.model.Utility
import band.effective.office.backend.feature.workspace.core.domain.model.Workspace
import band.effective.office.backend.feature.workspace.core.domain.model.WorkspaceZone
import java.time.Instant
import java.util.*

/**
 * Repository interface for workspace-related database operations.
 */
interface WorkspaceRepository {

    /**
     * Checks if a workspace with the given id exists.
     *
     * @param workspaceId id of requested workspace
     * @return true if [Workspace] with the given [workspaceId] exists in the database
     */
    fun workspaceExistsById(workspaceId: UUID): Boolean

    /**
     * Checks if a utility with the given id exists.
     *
     * @param utilityId id of requested utility
     * @return true if [Utility] with the given [utilityId] exists in the database
     */
    fun utilityExistsById(utilityId: UUID): Boolean

    /**
     * Returns all workspace utilities by workspace id.
     *
     * @param workspaceId id of the workspace
     * @return List of [Utility] for [Workspace] with the given id
     */
    fun findUtilitiesByWorkspaceId(workspaceId: UUID): List<Utility>

    /**
     * Finds utilities for multiple workspaces in a single query.
     *
     * @param ids Collection of workspace ids
     * @return HashMap mapping workspace ids to lists of utilities
     */
    fun findAllUtilitiesByWorkspaceIds(ids: Collection<UUID>): Map<UUID, List<Utility>>

    /**
     * Retrieves a workspace by its id.
     *
     * @param workspaceId id of requested workspace
     * @return [Workspace] with the given [workspaceId] or null if workspace with the given id doesn't exist
     */
    fun findById(workspaceId: UUID): Workspace?

    /**
     * Returns all workspaces with the given tag.
     *
     * @param tag tag name of requested workspaces
     * @return List of [Workspace] with the given [tag]
     */
    fun findAllByTag(tag: String): List<Workspace>

    /**
     * Returns all workspaces with the given tag which are free during the given period.
     *
     * @param tag tag name of requested workspaces
     * @param beginTimestamp period start time
     * @param endTimestamp period end time
     * @return List of [Workspace] with the given [tag]
     */
    fun findAllFreeByPeriod(tag: String, beginTimestamp: Instant, endTimestamp: Instant): List<Workspace>

    /**
     * Returns all workspace zones.
     *
     * @return List of all [WorkspaceZone]
     */
    fun findAllZones(): List<WorkspaceZone>
}