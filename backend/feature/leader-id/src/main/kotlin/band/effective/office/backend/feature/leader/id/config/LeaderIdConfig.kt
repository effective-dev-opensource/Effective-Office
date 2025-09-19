package band.effective.office.backend.feature.leader.id.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for the LeaderId module.
 * 
 * This configuration class sets up all the necessary beans for the LeaderId
 * feature module using @ConfigurationProperties for type-safe configuration.
 */
@Configuration
class LeaderIdConfig {

    /**
     * Creates LeaderId parameters bean with configuration values.
     */
    @Bean
    fun leaderIdParameters(): LeaderIdParameters {
        return LeaderIdParameters()
    }
}

/**
 * Data class for LeaderId configuration parameters.
 * Uses @ConfigurationProperties for type-safe configuration binding.
 */
@ConfigurationProperties(prefix = "leaderid")
data class LeaderIdParameters(
    var eventsPeriodDays: Int = 14,
    var defaultCityId: Int = 893,
    var defaultPlaceId: Int = 3942,
    var defaultPaginationSize: Int = 100
)
