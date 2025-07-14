package band.effective.office.backend.feature.calendar.subscription.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * JPA entity representing a Google Calendar notification channel in the database.
 */
@Entity
@Table(name = "calendar_channels")
class ChannelEntity(
    @Id
    @Column(name = "calendar_id", nullable = false)
    val calendarId: String,

    @Column(name = "channel_id", nullable = false)
    val channelId: String,

    @Column(name = "resource_id", nullable = false)
    val resourceId: String,

    @Column(nullable = true)
    val type: String,

    @Column(nullable = false)
    val address: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)