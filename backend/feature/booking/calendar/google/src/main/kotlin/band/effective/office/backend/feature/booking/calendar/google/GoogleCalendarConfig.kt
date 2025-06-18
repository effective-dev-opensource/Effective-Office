package band.effective.office.backend.feature.booking.calendar.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader

/**
 * Configuration for Google Calendar API client.
 * This configuration is only active when the calendar.provider property is set to "google".
 */
@Configuration
@ConditionalOnProperty(name = ["calendar.provider"], havingValue = "google")
class GoogleCalendarConfig(
    private val resourceLoader: ResourceLoader
) {

    @Value("\${calendar.application-name}")
    private lateinit var applicationName: String

    @Value("\${calendar.delegated-user}")
    private lateinit var delegatedUser: String

    @Value("\${calendar.credentials.file}")
    private lateinit var credentialsFile: String

    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

    /**
     * Creates a Google Calendar API client with OAuth2 credentials.
     */
    @Bean
    fun httpTransport(): HttpTransport {
        return GoogleNetHttpTransport.newTrustedTransport()
    }

    @Bean
    fun googleCredentials(): GoogleCredentials {
        val credentials = GoogleCredentials
            .fromStream(resourceLoader.getResource(credentialsFile).inputStream)
            .createScoped(CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_EVENTS)

        return if (delegatedUser.isNotBlank()) {
            credentials.createDelegated(delegatedUser)
        } else {
            credentials
        }
    }

    @Bean
    fun calendar(httpTransport: HttpTransport, googleCredentials: GoogleCredentials): Calendar {
        return Calendar.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(googleCredentials))
            .setApplicationName(applicationName)
            .build()
    }
}
