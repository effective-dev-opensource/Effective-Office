package band.effective.office.backend.feature.user.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

/**
 * JPA entity representing a user in the database.
 */
@Entity
@Table(name = "users")
class UserEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = false, length = 255)
    val username: String,

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Column(name = "first_name", nullable = false, length = 255)
    val firstName: String,

    @Column(name = "last_name", nullable = false, length = 255)
    val lastName: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val active: Boolean = true
)
