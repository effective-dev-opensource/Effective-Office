package band.effective.office.backend.feature.workspace.core.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

/**
 * JPA entity representing a calendar ID linked to a workspace.
 */
@Entity
@Table(name = "calendar_ids")
data class CalendarIdEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false, unique = true)
    val workspace: WorkspaceEntity,
    
    @Column(name = "calendar_id", nullable = false, unique = true, length = 255)
    val calendarId: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)