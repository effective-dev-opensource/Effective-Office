package band.effective.office.backend.feature.photo.saver.provider.mattermost.config

import band.effective.office.backend.feature.photo.saver.core.util.HttpServiceProxyFactoryBuilder
import band.effective.office.backend.feature.photo.saver.provider.mattermost.api.MattermostApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuration for Mattermost HTTP client (WebClient-based).
 */
@Configuration
class MattermostClientConfig {

    companion object {
        private const val MAX_IN_MEMORY_SIZE_50MB = 50 * 1024 * 1024 // 50MB for file downloads
    }

    @Bean("photoSaverMattermostApi")
    fun mattermostApi(
        @Qualifier("photoSaverMattermostCredentials") mattermostCredentials: MattermostCredentials
    ): MattermostApi {
        val webClient = WebClient.builder()
            .baseUrl(mattermostCredentials.baseUrl)
            .defaultHeader("X-Requested-With", "XMLHttpRequest")
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Authorization", mattermostCredentials.token)
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_50MB)
            }
            .build()

        return HttpServiceProxyFactoryBuilder.createClient(webClient, MattermostApi::class.java)
    }
}
