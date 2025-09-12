package band.effective.office.backend.feature.notifications.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

/**
 * JPA entity for devices with kiosk mode functionality.
 */
@Entity
@Table(name = "devices")
class DeviceEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "device_id", nullable = false, unique = true, length = 255)
    val deviceId: String,

    @Column(nullable = false, length = 255)
    val tag: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)