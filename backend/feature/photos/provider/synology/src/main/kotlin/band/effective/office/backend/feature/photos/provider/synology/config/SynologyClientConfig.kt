package band.effective.office.backend.feature.photos.provider.synology.config

import band.effective.office.backend.feature.photos.provider.synology.api.SynologyApi
import band.effective.office.backend.feature.photos.provider.synology.constants.SynologyApiConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class SynologyClientConfig {
    @Bean
    fun synologyApi(synologyCredentials: SynologyCredentials): SynologyApi {
        val webClient = WebClient.builder()
            .baseUrl(synologyCredentials.url)
            .defaultHeader("User-Agent", SynologyApiConstants.USER_AGENT)
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
            }
            .build()

        val adapter = WebClientAdapter.create(webClient)
        val factory = HttpServiceProxyFactory
            .builder()
            .exchangeAdapter(adapter)
            .build()

        return factory.createClient(SynologyApi::class.java)
    }
}