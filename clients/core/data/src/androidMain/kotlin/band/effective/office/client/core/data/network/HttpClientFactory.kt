package band.effective.office.client.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android-specific implementation of HttpClientFactory
 */
actual object HttpClientFactory {
    /**
     * Creates an Android-specific HTTP client using OkHttp engine
     * @return HttpClient instance
     */
    actual fun createHttpClient(): HttpClient = HttpClient(OkHttp)
}