package band.effective.office.backend.feature.workspace.core.service

import band.effective.office.backend.core.domain.model.Workspace as DomainWorkspace
import band.effective.office.backend.core.domain.model.WorkspaceZone as DomainWorkspaceZone
import band.effective.office.backend.core.domain.model.Utility as DomainUtility
import band.effective.office.backend.core.domain.service.WorkspaceDomainService
import band.effective.office.backend.feature.workspace.core.domain.model.Workspace
import band.effective.office.backend.feature.workspace.core.domain.model.WorkspaceZone
import band.effective.office.backend.feature.workspace.core.domain.model.Utility
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

/**
 * Adapter that implements the [WorkspaceDomainService] interface and delegates to [WorkspaceService].
 * This adapter converts between the feature module's model types and the core domain module's model types.
 */
@Service
class WorkspaceDomainServiceAdapter(private val workspaceService: WorkspaceService) : WorkspaceDomainService {

    /**
     * Retrieves a Workspace model by its id
     *
     * @param id id of requested workspace
     * @return [DomainWorkspace] with the given [id] or null if workspace with the given id doesn't exist
     */
    override fun findById(id: UUID): DomainWorkspace? {
        val workspace = workspaceService.findById(id)
        return workspace?.toDomainWorkspace()
    }

    /**
     * Returns all workspaces with the given tag
     *
     * @param tag tag name of requested workspaces
     * @return List of [DomainWorkspace] with the given [tag]
     */
    override fun findAllByTag(tag: String): List<DomainWorkspace> {
        val workspaces = workspaceService.findAllByTag(tag)
        return workspaces.map { it.toDomainWorkspace() }
    }

    /**
     * Returns all workspaces with the given tag which are free during the given period
     *
     * @param tag tag name of requested workspaces
     * @param beginTimestamp period start time
     * @param endTimestamp period end time
     * @return List of [DomainWorkspace] with the given [tag]
     */
    override fun findAllFreeByPeriod(tag: String, beginTimestamp: Instant, endTimestamp: Instant): List<DomainWorkspace> {
        val workspaces = workspaceService.findAllFreeByPeriod(tag, beginTimestamp, endTimestamp)
        return workspaces.map { it.toDomainWorkspace() }
    }

    /**
     * Returns all workspace zones
     *
     * @return List of all [DomainWorkspaceZone]
     */
    override fun findAllZones(): List<DomainWorkspaceZone> {
        val zones = workspaceService.findAllZones()
        return zones.map { it.toDomainWorkspaceZone() }
    }

    /**
     * Converts a feature module Workspace to a core domain Workspace
     */
    private fun Workspace.toDomainWorkspace(): DomainWorkspace {
        return DomainWorkspace(
            id = this.id,
            name = this.name,
            tag = this.tag,
            utilities = this.utilities.map { it.toDomainUtility() },
            zone = this.zone?.toDomainWorkspaceZone()
        )
    }

    /**
     * Converts a feature module WorkspaceZone to a core domain WorkspaceZone
     */
    private fun WorkspaceZone.toDomainWorkspaceZone(): DomainWorkspaceZone {
        return DomainWorkspaceZone(
            id = this.id,
            name = this.name
        )
    }

    /**
     * Converts a feature module Utility to a core domain Utility
     */
    private fun Utility.toDomainUtility(): DomainUtility {
        return DomainUtility(
            id = this.id,
            name = this.name,
            iconUrl = this.iconUrl,
            count = this.count
        )
    }
}