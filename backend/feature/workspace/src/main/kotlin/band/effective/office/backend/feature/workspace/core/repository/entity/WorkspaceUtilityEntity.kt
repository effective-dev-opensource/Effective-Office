package band.effective.office.backend.feature.workspace.core.repository.entity

import jakarta.persistence.*

@Entity
@Table(name = "workspace_utilities")
data class WorkspaceUtilityEntity(
    @Id
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceEntity,

    @Id
    @ManyToOne
    @JoinColumn(name = "utility_id")
    val utility: UtilityEntity,

    @Column(name = "count", nullable = false)
    val count: Int
)