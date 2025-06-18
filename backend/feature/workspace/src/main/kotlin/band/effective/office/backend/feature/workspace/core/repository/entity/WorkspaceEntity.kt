package band.effective.office.backend.feature.workspace.core.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "workspaces")
data class WorkspaceEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false, unique = true, length = 255)
    val name: String,
    @Column(name = "tag", nullable = false, unique = true, length = 255)
    val tag: String,
    @ManyToMany
    @JoinTable(
        name = "workspace_utilities",
        joinColumns = [JoinColumn(name = "workspace_id")],
        inverseJoinColumns = [JoinColumn(name = "utility_id")]
    )
    val utilities: List<UtilityEntity> = emptyList(),

    @ManyToOne
    @JoinColumn(name = "zone_id")
    var zone: WorkspaceZoneEntity? = null
)