package band.effective.office.backend.feature.authorization.apikey.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

/**
 * Entity representing an API key stored in the database.
 * The key value is stored as a hashed string for security.
 */
@Entity
@Table(name = "api_keys")
data class ApiKey(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    
    @Column(name = "key_value", nullable = false, unique = true)
    val keyValue: String,
    
    @Column(name = "description")
    val description: String? = null,
    
    @Column(name = "user_id")
    val userId: UUID? = null
)