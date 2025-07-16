package band.effective.office.backend.feature.workspace.core.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.OneToMany
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "utilities")
data class UtilityEntity(
    @Id
    val id: UUID,
    @Column(name = "name", nullable = false, unique = true, length = 255)
    val name: String,
    @Column(name = "icon_url", nullable = false, unique = true, length = 255)
    val iconUrl: String,

    @OneToMany(mappedBy = "utility")
    val workspaceUtilities: List<WorkspaceUtilityEntity> = emptyList()
)
