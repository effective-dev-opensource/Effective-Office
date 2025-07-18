package band.effective.office.backend.feature.workspace.core.repository.mapper

import band.effective.office.backend.core.domain.model.Utility
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.model.WorkspaceZone
import band.effective.office.backend.feature.workspace.core.repository.entity.WorkspaceEntity
import band.effective.office.backend.feature.workspace.core.repository.entity.WorkspaceUtilityEntity
import org.springframework.stereotype.Component

@Component
object WorkspaceMapper {

    fun toDomain(entity: WorkspaceEntity): Workspace = Workspace(
        id = entity.id,
        name = entity.name,
        tag = entity.tag,
        utilities = entity.workspaceUtilities.map { toDomain(it) },
        zone = entity.zone?.let { WorkspaceZone(it.id, it.name) },
    )

    private fun toDomain(entity: WorkspaceUtilityEntity): Utility = Utility(
        id = entity.utility.id,
        name = entity.utility.name,
        iconUrl = entity.utility.iconUrl,
        count = entity.count
    )
}