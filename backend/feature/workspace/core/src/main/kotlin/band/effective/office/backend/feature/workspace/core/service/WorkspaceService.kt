package band.effective.office.backend.feature.workspace.core.service

import band.effective.office.backend.feature.workspace.core.domain.model.Workspace
import band.effective.office.backend.feature.workspace.core.domain.model.WorkspaceZone
import java.time.Instant
import java.util.*

/**
 * Interface that provides methods for manipulating [Workspace] objects.
 */
interface WorkspaceService {

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
     * Returns all workspaces with the given tag which are free during the given period
     *
     * @param tag tag name of requested workspaces
     * @param beginTimestamp period start time
     * @param endTimestamp period end time
     * @return List of [Workspace] with the given [tag]
     */
    fun findAllFreeByPeriod(tag: String, beginTimestamp: Instant, endTimestamp: Instant): List<Workspace>

    /**
     * Returns all workspace zones
     *
     * @return List of all [WorkspaceZone]
     */
    fun findAllZones(): List<WorkspaceZone>
}