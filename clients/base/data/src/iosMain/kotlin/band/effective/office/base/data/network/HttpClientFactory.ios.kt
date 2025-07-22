package band.effective.office.base.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual object HttpClientFactory {
    actual fun createHttpClient(): HttpClient = HttpClient(Darwin)
}