package band.effective.office.backend.feature.photo.saver.core.config

import band.effective.office.backend.feature.photo.saver.core.util.SystemTimeProvider
import band.effective.office.backend.feature.photo.saver.core.util.TimeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for time-related beans.
 */
@Configuration
class TimeConfig {
    
    @Bean
    fun timeProvider(): TimeProvider = SystemTimeProvider()
}
