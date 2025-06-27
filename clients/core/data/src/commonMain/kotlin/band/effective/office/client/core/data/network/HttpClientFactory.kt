package band.effective.office.client.core.data.network

import io.ktor.client.HttpClient

/**
 * Factory for creating platform-specific HTTP clients
 */
expect object HttpClientFactory {
    /**
     * Creates a platform-specific HTTP client
     * @return HttpClient instance
     */
    fun createHttpClient(): HttpClient
}