package band.effective.office.tablet.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

/**
 * iOS-specific implementation of HttpClientFactory
 */
actual object HttpClientFactory {
    /**
     * Creates an iOS-specific HTTP client using Darwin engine
     * @return HttpClient instance
     */
    actual fun createHttpClient(): HttpClient = HttpClient(Darwin)
}