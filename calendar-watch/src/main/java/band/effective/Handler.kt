package band.effective

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Channel
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.util.StringJoiner
import java.util.UUID
import java.util.function.Function

/**
 * Handler for Yandex Cloud Functions
 */
class Handler : Function<Unit, Unit> {
    override fun apply(p0: Unit) = subscribeOnNotifications()
}

/**
 * For testing on local machine
 */
fun main (): Unit = subscribeOnNotifications()

/**
 * Subscribe to all owned calendars. Should be called every 7 days.
 */
fun subscribeOnNotifications() {
    val logger : Logger = LoggerFactory.getLogger(JsonFactory::class.java)

    val calendarService : Calendar = createCalendarService()

    val calendars = calendarService.calendarList().list().execute().items

    for (calendar in calendars) {
        if (calendar.accessRole == "owner") {
            val appAddress = if (!AppConstants.TEST_CALENDARS.contains(calendar.id)) {
                AppConstants.APPLICATION_URL
            } else {
                AppConstants.TEST_APPLICATION_URL
            }

            val channel = Channel().apply {
                id = UUID.randomUUID().toString()
                type = "web_hook"
                address = "$appAddress/notifications"
            }
            try {
                calendarService.events().watch(calendar.id, channel).execute()
                logger.info("Subscribed on notifications from ${calendar.id} calendar")
            } catch (e: Exception) {
                logger.error("Can't subscribe on notifications from ${calendar.id} calendar", e)
            }
        }
    }
}

private fun createCalendarService() : Calendar {
    val jsonFactory : JsonFactory = GsonFactory.getDefaultInstance()
    val httpTransport : HttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    val authorGoogleAccount: String = AppConstants.DEFAULT_APP_EMAIL
    val inputStream: ByteArrayInputStream = ByteArrayInputStream(AppConstants.JSON_GOOGLE_CREDENTIALS.toByteArray())
    val googleCredentials : GoogleCredentials = GoogleCredentials.fromStream(inputStream).createScoped(CalendarScopes.CALENDAR)
        .createDelegated(authorGoogleAccount)

    return Calendar.Builder(
        httpTransport,
        jsonFactory,
        HttpCredentialsAdapter(googleCredentials)
    ).setApplicationName("APPLICATION_NAME").build()
}
