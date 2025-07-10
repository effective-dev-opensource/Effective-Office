package band.effective.office.backend.feature.calendar.subscription.scheduler

import band.effective.office.backend.feature.calendar.subscription.config.CalendarSubscriptionConfig
import band.effective.office.backend.feature.calendar.subscription.service.GoogleCalendarService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Scheduler for periodically subscribing to calendar notifications.
 * Google Calendar notification channels expire after 7 days, so we need to renew them regularly.
 */
@Component
@EnableScheduling
class CalendarSubscriptionScheduler(
    private val googleCalendarService: GoogleCalendarService,
    private val config: CalendarSubscriptionConfig
) {

    private val logger = LoggerFactory.getLogger(CalendarSubscriptionScheduler::class.java)

    /**
     * Subscribes to calendar notifications for both production and test environments.
     * Runs every 6 days to ensure subscriptions don't expire (Google Calendar subscriptions expire after 7 days).
     */
    @Scheduled(fixedRate = 6 * 24 * 60 * 60 * 1000) // 6 days in milliseconds
    fun subscribeToCalendarNotifications() {
        logger.info("Starting calendar subscription process")

        // Subscribe to production calendars
        val productionCalendars = config.getCalendars()
        logger.debug("productionCalendars: {}", productionCalendars)
        logger.debug("applicationUrl: {}", config.applicationUrl)
        if (config.applicationUrl.isNotBlank() && productionCalendars.isNotEmpty()) {
            googleCalendarService.subscribeToCalendarNotifications(config.applicationUrl, productionCalendars)
        } else {
            logger.warn("Production calendar subscription skipped: missing configuration")
        }

        // Subscribe to test calendars
        val testCalendars = config.getTestCalendars()
        logger.debug("testCalendars: {}", testCalendars)
        logger.debug("testApplicationUrl: {}", config.testApplicationUrl)
        if (config.testApplicationUrl.isNotBlank() && testCalendars.isNotEmpty()) {
            googleCalendarService.subscribeToCalendarNotifications(config.testApplicationUrl, testCalendars)
        } else {
            logger.warn("Test calendar subscription skipped: missing configuration")
        }

        logger.info("Calendar subscription process completed")
    }

    /**
     * Initializes subscriptions on application startup.
     */
    fun init() {
        logger.info("Initializing calendar subscriptions")
        subscribeToCalendarNotifications()
    }
}
