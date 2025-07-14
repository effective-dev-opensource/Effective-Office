package band.effective.office.backend.feature.booking.core.config

import band.effective.office.backend.feature.booking.core.domain.CalendarProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration for selecting the appropriate CalendarProvider based on the configuration.
 */
@Configuration
class CalendarProviderConfig {

    /**
     * Provides the primary CalendarProvider bean based on the configuration.
     * This bean will be injected into the BookingService.
     *
     * @param googleCalendarProvider The Google Calendar provider (if available)
     * @param dummyCalendarProvider The dummy calendar provider (if available)
     * @return The selected CalendarProvider
     */
    @Bean
    @Primary
    fun calendarProvider(
        @Qualifier("googleCalendarProvider") googleCalendarProvider: CalendarProvider?,
        @Qualifier("dummyCalendarProvider") dummyCalendarProvider: CalendarProvider?
    ): CalendarProvider {
        // Use the Google Calendar provider if available, otherwise use the dummy provider
        return googleCalendarProvider ?: dummyCalendarProvider
            ?: throw IllegalStateException("No CalendarProvider implementation available")
    }
}