package band.effective.office.backend.feature.sport.core.config

import band.effective.office.backend.feature.sport.core.domain.SportProvider
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration for selecting the appropriate SportProvider based on the configuration.
 */
@Configuration
class SportProviderConfig {

    @Value("\${sport.provider:dummy}")
    private lateinit var providerType: String

    /**
     * Provides the primary SportProvider bean based on the configuration.
     * This bean will be injected into the SportService.
     *
     * @param dummySportProvider The dummy sport provider (if available)
     * @return The selected SportProvider
     */
    @Bean
    @Primary
    fun sportProvider(
        clockifySportProvider: ObjectProvider<SportProvider>,
        dummySportProvider: ObjectProvider<SportProvider>
    ): SportProvider {
        return when (providerType.lowercase()) {
            "clockify" -> {
                clockifySportProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Clockify SportProvider not available. Check configuration and dependencies.") }
            }
            "dummy" -> {
                dummySportProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Dummy SportProvider not available. Check configuration and dependencies.") }
            }
            else -> {
                throw IllegalStateException("Unknown sport provider type: $providerType. Supported types: clockify, dummy")
            }
        }
    }
}
