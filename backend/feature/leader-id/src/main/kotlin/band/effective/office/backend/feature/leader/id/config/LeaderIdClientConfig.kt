package band.effective.office.backend.feature.leader.id.config

import band.effective.office.backend.feature.leader.id.api.LeaderIdApi
import band.effective.office.backend.feature.leader.id.constants.LeaderIdApiConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

/**
 * Configuration for LeaderId HTTP client.
 * Creates a Spring HTTP Interface client for clean API communication.
 */
@Configuration
class LeaderIdClientConfig {

    @Bean
    fun leaderIdApi(): LeaderIdApi {
        val webClient = WebClient.builder()
            .baseUrl(LeaderIdApiConstants.API_BASE_URL)
            .defaultHeader("User-Agent", LeaderIdApiConstants.USER_AGENT)
            .defaultHeader("Accept", LeaderIdApiConstants.ACCEPT_HEADER)
            .build()

        val adapter = WebClientAdapter.create(webClient)
        val factory = HttpServiceProxyFactory
            .builder()
            .exchangeAdapter(adapter)
            .build()

        return factory.createClient(LeaderIdApi::class.java)
    }
}
