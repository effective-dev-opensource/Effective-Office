package band.effective.office.backend.feature.workspace.core.repository

import band.effective.office.backend.feature.workspace.core.repository.entity.WorkspaceEntity
import band.effective.office.backend.feature.workspace.core.repository.entity.WorkspaceZoneEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Repository interface for workspace-related database operations.
 */
@Repository
interface WorkspaceRepository : JpaRepository<WorkspaceEntity, UUID> {
    /**
     * Returns all workspaces with the given tag.
     *
     * @param tag tag name of requested workspaces
     * @return List of [Workspace] with the given [tag]
     */
    fun findAllByTag(tag: String): List<WorkspaceEntity>

    /**
     * Returns all workspace zones.
     *
     * @return List of all [WorkspaceZone]
     */
    @Query("SELECT z FROM WorkspaceZoneEntity z")
    fun findAllWorkspaceZones(): List<WorkspaceZoneEntity>
}
