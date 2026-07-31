package band.effective.office.shared.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.curl.Curl

actual object HttpClientFactory {
    actual fun createHttpClient(): HttpClient = HttpClient(Curl)
}
