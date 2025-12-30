package band.effective.office.backend.feature.photo.saver.storage.synology.config

import band.effective.office.backend.feature.photo.saver.core.util.HttpServiceProxyFactoryBuilder
import band.effective.office.backend.feature.photo.saver.storage.synology.api.SynologyApi
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

/**
 * Configuration for Synology HTTP client (WebClient-based) for Photo Saver storage.
 */
@Configuration("photoSaverSynologyClientConfig")
class SynologyClientConfig {

    companion object {
        private const val MAX_IN_MEMORY_SIZE_10MB = 10 * 1024 * 1024 // 10MB for file uploads
    }

    @Bean("photoSaverSynologyApi")
    fun synologyApi(
        @Qualifier("photoSaverSynologyCredentials") synologyCredentials: SynologyCredentials
    ): SynologyApi {
        // FIXME: Disable SSL verification temporarily due to expired certificate
        val sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()
        
        val httpClient = HttpClient.create()
            .secure { it.sslContext(sslContext) }
        
        val webClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(synologyCredentials.baseUrl)
            .defaultHeader("User-Agent", "Mozilla/5.0")
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_10MB)
            }
            .build()

        return HttpServiceProxyFactoryBuilder.createClient(webClient, SynologyApi::class.java)
    }
}
