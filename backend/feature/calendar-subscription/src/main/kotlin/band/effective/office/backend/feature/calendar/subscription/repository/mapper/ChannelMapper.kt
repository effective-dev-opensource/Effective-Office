package band.effective.office.backend.feature.calendar.subscription.repository.mapper

import band.effective.office.backend.feature.calendar.subscription.repository.entity.ChannelEntity
import com.google.api.services.calendar.model.Channel

/**
 * Mapper for converting between Google Calendar Channel and ChannelEntity.
 */
object ChannelMapper {
    /**
     * Converts a Google Calendar Channel to a ChannelEntity.
     *
     * @param channel The Google Calendar Channel to convert
     * @param calendarId The ID of the calendar associated with the channel
     * @return The converted ChannelEntity
     */
    fun toEntity(channel: Channel, calendarId: String, address: String): ChannelEntity {
        return ChannelEntity(
            calendarId = calendarId,
            channelId = channel.id,
            resourceId = channel.resourceId,
            type = "web_hook",
            address = address,
        )
    }

    /**
     * Converts a ChannelEntity to a Google Calendar Channel.
     *
     * @param entity The ChannelEntity to convert
     * @return The converted Google Calendar Channel
     */
    fun toChannel(entity: ChannelEntity): Channel {
        return Channel().apply {
            id = entity.channelId
            resourceId = entity.resourceId
            type = entity.type
            address = entity.address
        }
    }
}