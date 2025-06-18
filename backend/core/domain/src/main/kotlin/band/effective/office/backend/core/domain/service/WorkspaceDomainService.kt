package band.effective.office.backend.core.domain.service

import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.model.WorkspaceZone
import java.time.Instant
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
}
