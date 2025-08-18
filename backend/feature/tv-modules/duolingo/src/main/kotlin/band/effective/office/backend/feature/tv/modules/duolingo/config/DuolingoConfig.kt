package band.effective.office.backend.feature.tv.modules.duolingo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuration for the Duolingo module.
 */
@Configuration
class DuolingoConfig {

    @Bean
    fun duolingoWebClient(): WebClient = WebClient.builder()
        .baseUrl("https://www.duolingo.com")
        .build()
}