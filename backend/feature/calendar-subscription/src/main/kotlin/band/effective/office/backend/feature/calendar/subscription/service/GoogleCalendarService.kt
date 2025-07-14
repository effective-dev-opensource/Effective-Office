package band.effective.office.backend.feature.calendar.subscription.service

import band.effective.office.backend.feature.calendar.subscription.config.CalendarSubscriptionConfig
import band.effective.office.backend.feature.calendar.subscription.repository.ChannelRepository
import band.effective.office.backend.feature.calendar.subscription.repository.mapper.ChannelMapper
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Channel
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

/**
 * Service for interacting with Google Calendar API.
 */
@Service
class GoogleCalendarService(
    private val config: CalendarSubscriptionConfig,
    private val resourceLoader: ResourceLoader,
    private val channelRepository: ChannelRepository
) {
    private val logger = LoggerFactory.getLogger(GoogleCalendarService::class.java)

    /**
     * Creates a Google Calendar service client.
     *
     * @return Configured Google Calendar service
     */
    fun createCalendarService(): Calendar {
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
        val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val authorGoogleAccount: String = config.defaultAppEmail
        val inputStream = resourceLoader.getResource(config.googleCredentials).inputStream
        val googleCredentials: GoogleCredentials =
            GoogleCredentials
                .fromStream(inputStream)
                .createScoped(CalendarScopes.CALENDAR)
                .createDelegated(authorGoogleAccount)

        return Calendar
            .Builder(
                httpTransport,
                jsonFactory,
                HttpCredentialsAdapter(googleCredentials),
            ).setApplicationName("Effective Office")
            .build()
    }

    /**
     * Stops an active notification channel for a calendar.
     *
     * @param calendarId ID of the calendar whose notification channel should be stopped
     */
    private fun stopChannel(calendarId: String, calendarService: Calendar) {
        val channelEntity = channelRepository.findByCalendarId(calendarId)
        if (channelEntity != null) {
            try {
                // Convert entity to Channel and stop it
                val channel = ChannelMapper.toChannel(channelEntity)
                logger.info("Trying to stop channel -> id: ${channel.id}, type:${channel.type}, address:${channel.address}, resourceId: ${channel.resourceId}")
                calendarService.channels().stop(channel).execute()

                // Remove from database
                channelRepository.delete(channelEntity)
                logger.info("Stopped notification channel for calendar $calendarId. Channel id: ${channelEntity.channelId}")
            } catch (e: Exception) {
                logger.error("Failed to stop notification channel for calendar $calendarId", e)
            }
        }
    }

    /**
     * Subscribes to notifications for the specified calendars.
     * Stops any existing channels before creating new ones.
     *
     * @param serverUrl URL of the server to receive notifications
     * @param calendarIds List of calendar IDs to subscribe to
     */
    fun subscribeToCalendarNotifications(
        serverUrl: String,
        calendarIds: List<String>,
    ) {
        val calendarService = createCalendarService()

        for (calendarId in calendarIds) {
            // Stop existing channel if any
            stopChannel(calendarId, calendarService)

            val channelUid = UUID.randomUUID().toString()
            val channel =
                Channel().apply {
                    id = channelUid
                    type = "web_hook"
                    address = "$serverUrl/notifications"
                }

            try {
                val createdChannel = calendarService.events().watch(calendarId, channel).execute()
                logger.info("Created notification channel id: ${createdChannel.id}, type:${createdChannel.type}, address:${createdChannel.address}, resourceId: ${createdChannel.resourceId}")
                // Store the created channel in the database
                val channelEntity = ChannelMapper.toEntity(createdChannel, calendarId, "$serverUrl/notifications") // Address is null in createdChannel, so we need to pass it explicitly.
                channelRepository.save(channelEntity)

                logger.info("Server with url $serverUrl was subscribed to notifications from $calendarId calendar. Channel id: $channelUid")
            } catch (e: Exception) {
                logger.error("Can't subscribe server with url $serverUrl to notifications from $calendarId calendar", e)
            }
        }
    }
}
