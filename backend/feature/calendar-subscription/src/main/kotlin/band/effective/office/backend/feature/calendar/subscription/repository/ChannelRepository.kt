package band.effective.office.backend.feature.calendar.subscription.repository

import band.effective.office.backend.feature.calendar.subscription.repository.entity.ChannelEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for accessing Google Calendar notification channel entities in the database.
 */
@Repository
interface ChannelRepository : JpaRepository<ChannelEntity, String> {
    /**
     * Find a channel by calendar ID.
     *
     * @param calendarId the calendar ID to search for
     * @return the channel entity if found, null otherwise
     */
    fun findByCalendarId(calendarId: String): ChannelEntity?

    /**
     * Find a channel by channel ID.
     *
     * @param channelId the channel ID to search for
     * @return the channel entity if found, null otherwise
     */
    fun findByChannelId(channelId: String): ChannelEntity?
}
