package band.effective.office.backend.feature.authorization.core.config

import band.effective.office.backend.feature.authorization.core.AuthorizationChain
import band.effective.office.backend.feature.authorization.core.Authorizer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Configuration for the authorization chain.
 * This class sets up the chain of authorizers using the Chain of Responsibility pattern.
 */
@Configuration
@ComponentScan("band.effective.office.backend.feature.authorization.core")
class AuthorizationConfig {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Creates and configures the authorization chain.
     * The chain is built with all available authorizers in the application context.
     *
     * @param authorizers All authorizers available in the application context
     * @return The configured authorization chain
     */
    @Bean
    fun authorizationChain(
        authorizers: List<Authorizer>
    ): AuthorizationChain {
        val chain = AuthorizationChain()

        // Add all authorizers to the chain
        authorizers.forEach { authorizer ->
            chain.addAuthorizer(authorizer)
            logger.info("Added authorizer to chain: {}", authorizer::class.simpleName)
        }

        return chain
    }
}
