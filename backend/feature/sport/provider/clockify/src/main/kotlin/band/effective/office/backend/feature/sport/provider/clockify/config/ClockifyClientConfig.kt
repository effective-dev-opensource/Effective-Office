package band.effective.office.backend.feature.sport.provider.clockify.config

import band.effective.office.backend.feature.sport.provider.clockify.api.ClockifyApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class ClockifyClientConfig {
    
    companion object {
        private const val CLOCKIFY_BASE_URL = "https://reports.api.clockify.me"
        
        /**
         * Maximum in-memory buffer size for WebClient codec (5 MB).
         * Clockify API detailed reports can return large JSON payloads with extensive time entry data,
         * especially for quarterly reports with many users .
         */
        private const val MAX_IN_MEMORY_SIZE = 5 * 1024 * 1024 // 5 MB
    }
    
    @Bean
    fun clockifyApi(): ClockifyApi {
        val webClient = WebClient.builder()
            .baseUrl(CLOCKIFY_BASE_URL)
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE)
            }
            .build()

        val adapter = WebClientAdapter.create(webClient)
        val factory = HttpServiceProxyFactory
            .builder()
            .exchangeAdapter(adapter)
            .build()

        return factory.createClient(ClockifyApi::class.java)
    }
}