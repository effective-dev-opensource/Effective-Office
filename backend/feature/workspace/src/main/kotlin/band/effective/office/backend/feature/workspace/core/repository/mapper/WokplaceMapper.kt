package band.effective.office.backend.feature.workspace.core.repository.mapper

import band.effective.office.backend.core.domain.model.Utility
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.model.WorkspaceZone
import band.effective.office.backend.feature.workspace.core.repository.entity.UtilityEntity
import band.effective.office.backend.feature.workspace.core.repository.entity.WorkspaceEntity
import org.springframework.stereotype.Component

@Component
object WokplaceMapper {

    fun toDomain(entity: WorkspaceEntity): Workspace = Workspace(
        id = entity.id,
        name = entity.name,
        tag = entity.tag,
        utilities = entity.utilities.map(WokplaceMapper::toDomain),
        zone = entity.zone?.let { WorkspaceZone(it.id, it.name) },
    )

    private fun toDomain(entity: UtilityEntity): Utility = Utility(
        id = entity.id,
        name = entity.name,
        iconUrl = entity.iconUrl,
        count = 0, // TODO
    )
}