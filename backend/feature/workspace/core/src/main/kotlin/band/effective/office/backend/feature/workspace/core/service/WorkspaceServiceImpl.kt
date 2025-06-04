package band.effective.office.backend.feature.workspace.core.service

import band.effective.office.backend.feature.workspace.core.domain.model.Workspace
import band.effective.office.backend.feature.workspace.core.domain.model.WorkspaceZone
import band.effective.office.backend.feature.workspace.core.repository.WorkspaceRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

/**
 * Implementation of the [WorkspaceService] interface.
 */
@Service
class WorkspaceServiceImpl(private val repository: WorkspaceRepository) : WorkspaceService {

    /**
     * Retrieves a Workspace model by its id
     *
     * @param id id of requested workspace
     * @return [Workspace] with the given [id] or null if workspace with the given id doesn't exist
     */
    override fun findById(id: UUID): Workspace? {
        return repository.findById(id)
    }

    /**
     * Returns all workspaces with the given tag
     *
     * @param tag tag name of requested workspaces
     * @return List of [Workspace] with the given [tag]
     */
    override fun findAllByTag(tag: String): List<Workspace> {
        return repository.findAllByTag(tag)
    }

    /**
     * Returns all workspaces with the given tag which are free during the given period
     *
     * @param tag tag name of requested workspaces
     * @param beginTimestamp period start time
     * @param endTimestamp period end time
     * @return List of [Workspace] with the given [tag]
     */
    override fun findAllFreeByPeriod(tag: String, beginTimestamp: Instant, endTimestamp: Instant): List<Workspace> {
        return repository.findAllFreeByPeriod(tag, beginTimestamp, endTimestamp)
    }

    /**
     * Returns all workspace zones
     *
     * @return List of all [WorkspaceZone]
     */
    override fun findAllZones(): List<WorkspaceZone> {
        return repository.findAllZones()
    }
}
