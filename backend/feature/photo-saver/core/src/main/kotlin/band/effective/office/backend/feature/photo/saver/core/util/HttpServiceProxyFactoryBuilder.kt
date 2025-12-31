package band.effective.office.backend.feature.photo.saver.core.util

import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

/**
 * Utility for creating HttpServiceProxyFactory instances.
 * Eliminates code duplication across different HTTP client configurations.
 */
object HttpServiceProxyFactoryBuilder {
    
    /**
     * Creates HttpServiceProxyFactory from WebClient.
     * 
     * @param webClient Configured WebClient instance
     * @return HttpServiceProxyFactory ready to create HTTP service clients
     */
    fun create(webClient: WebClient): HttpServiceProxyFactory {
        val adapter = WebClientAdapter.create(webClient)
        return HttpServiceProxyFactory
            .builder()
            .exchangeAdapter(adapter)
            .build()
    }
    
    /**
     * Creates HTTP service client of specified type.
     * 
     * @param webClient Configured WebClient instance
     * @param serviceClass Service interface class
     * @return HTTP service client implementation
     */
    fun <T> createClient(webClient: WebClient, serviceClass: Class<T>): T {
        return create(webClient).createClient(serviceClass)
    }
}
