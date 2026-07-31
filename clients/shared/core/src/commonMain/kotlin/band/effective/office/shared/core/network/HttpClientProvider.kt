package band.effective.office.shared.core.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.Qualifier

/**
 * HTTP client provider that creates a configured HttpClient instance
 * 
 * Usage: Add to Koin module:
 * ```
 * single { ApiConfig(url = "https://api.example.com", key = "your-api-key") }
 * single { HttpClientProvider }
 * ```
 */
object HttpClientProvider : KoinComponent {
    inline fun <reified T : Any> getOrNull( qualifier: Qualifier? = null,) = runCatching { get<T>(qualifier) }.getOrNull()

    /**
     * Creates a configured HttpClient instance
     * @return HttpClient instance with timeout, content negotiation, and logging configurations
     */
    fun create(): HttpClient {
        return HttpClientFactory.createHttpClient().config {
            install(HttpTimeout) {
                requestTimeoutMillis = 40_000L    // 60s
                connectTimeoutMillis = 40_000L    // 30s
                socketTimeoutMillis  = 40_000L    // 60s (matches the read timeout)
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }

            val apiConfig = getOrNull<ApiConfig>()

            if (apiConfig != null) {
                install(DefaultRequest) {
                    headers.append(HttpHeaders.Authorization, "Bearer ${apiConfig.key}")
                    url(apiConfig.url)
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.log(
                            priority = io.github.aakira.napier.LogLevel.WARNING,
                            tag = "HttpClient",
                            null,
                            message,
                        )
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}
