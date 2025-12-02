package band.effective.office.tv.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

/**
 * Desktop-specific implementation of HttpClientFactory
 */
actual object HttpClientFactory {
    /**
     * Creates a Desktop-specific HTTP client using CIO engine
     * @return HttpClient instance
     */
    actual fun createHttpClient(): HttpClient = HttpClient(CIO)
}


