package band.effective.office.backend.feature.teammates.core.config

import band.effective.office.backend.feature.teammates.core.domain.TeammateProvider
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Configuration for selecting the appropriate TeammateProvider based on the configuration.
 */
@Configuration
class TeammateProviderConfig {

    @Value("\${teammates.provider:dummy}")
    private lateinit var providerType: String

    /**
     * Provides the primary TeammateProvider bean based on the configuration.
     * This bean will be injected into the TeammateService.
     *
     * @param notionTeammateProvider The Notion teammate provider (if available)
     * @param dummyTeammateProvider The dummy teammate provider (if available)
     * @return The selected TeammateProvider
     */
    @Bean
    @Primary
    fun teammateProvider(
        notionTeammateProvider: ObjectProvider<TeammateProvider>,
        dummyTeammateProvider: ObjectProvider<TeammateProvider>
    ): TeammateProvider {
        return when (providerType.lowercase()) {
            "notion" -> {
                notionTeammateProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Notion TeammateProvider not available. Check configuration and dependencies.") }
            }
            "dummy" -> {
                dummyTeammateProvider.orderedStream().findFirst()
                    .orElseThrow { IllegalStateException("Dummy TeammateProvider not available. Check configuration and dependencies.") }
            }
            else -> {
                throw IllegalStateException("Unknown teammate provider type: $providerType. Supported types: notion, dummy")
            }
        }
    }
}