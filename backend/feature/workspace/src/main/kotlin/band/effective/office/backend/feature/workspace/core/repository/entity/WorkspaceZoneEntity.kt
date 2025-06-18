package band.effective.office.backend.feature.workspace.core.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "workspace_zones")
data class WorkspaceZoneEntity(
    @Id
    var id: UUID,
    @Column(name = "name", nullable = false, unique = true, length = 255)
    var name: String
)