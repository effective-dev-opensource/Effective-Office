package band.effective.office.backend.feature.calendar.subscription

import band.effective.office.backend.feature.calendar.subscription.scheduler.CalendarSubscriptionScheduler
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for the calendar subscription module.
 * This class enables component scanning and configuration properties for the module.
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties
class CalendarSubscriptionModule(
    private val scheduler: CalendarSubscriptionScheduler,
) {
    /**
     * Initializes the calendar subscription module on application startup.
     */
    @PostConstruct
    fun init() {
        scheduler.init()
    }
}
