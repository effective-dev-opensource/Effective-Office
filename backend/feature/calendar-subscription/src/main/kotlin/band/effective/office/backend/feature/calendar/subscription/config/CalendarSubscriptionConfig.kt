package band.effective.office.backend.feature.calendar.subscription.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration properties for the calendar subscription feature.
 */
@Configuration
@ConfigurationProperties(prefix = "calendar.subscription")
data class CalendarSubscriptionConfig(
    /**
     * Default application email used for Google Calendar API.
     */
    var defaultAppEmail: String = "",
    /**
     * JSON credentials for Google API authentication.
     */
    var googleCredentials: String = "",
    /**
     * Application URL for production environment.
     */
    var applicationUrl: String = "",
    /**
     * Application URL for test environment.
     */
    var testApplicationUrl: String = "",
    /**
     * Comma-separated list of calendar IDs for production environment.
     * This is converted to a List<String> when accessed via the getCalendars() method.
     */
    var calendars: String = "",
    /**
     * Comma-separated list of calendar IDs for test environment.
     * This is converted to a List<String> when accessed via the getTestCalendars() method.
     */
    var testCalendars: String = "",
) {
    /**
     * Gets the list of calendar IDs for production environment.
     * Converts the comma-separated string to a list.
     */
    fun getCalendars(): List<String> = if (calendars.isBlank()) emptyList() else calendars.split(",").map { it.trim() }

    /**
     * Gets the list of calendar IDs for test environment.
     * Converts the comma-separated string to a list.
     */
    fun getTestCalendars(): List<String> = if (testCalendars.isBlank()) emptyList() else testCalendars.split(",").map { it.trim() }
}
