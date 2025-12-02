package band.effective.office.tv.core.data.network

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
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * HTTP client provider that creates a configured HttpClient instance
 */
object HttpClientProvider : KoinComponent {

    private val apiKey by inject<String>(named("ApiKey"))
    private val apiUrl by inject<String>(named("ApiUrl"))
    private val isDebug by inject<Boolean>(named("IsDebug"))

    /**
     * Creates a configured HttpClient instance
     * @return HttpClient instance with timeout, content negotiation, and logging configurations
     */
    fun create(): HttpClient {
        return HttpClientFactory.createHttpClient().config {
            install(HttpTimeout) {
                // Backend API responds within 1-3 seconds.
                // 10 seconds is sufficient even for slow connections.
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        coerceInputValues = true
                    }
                )
            }
            install(DefaultRequest) {
                if (apiKey.isNotBlank()) {
                    headers.append(HttpHeaders.Authorization, "Bearer $apiKey")
                }
                if (apiUrl.isNotBlank()) {
                    url(apiUrl)
                }
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.log(
                            priority = if (isDebug) {
                                io.github.aakira.napier.LogLevel.DEBUG
                            } else {
                                io.github.aakira.napier.LogLevel.INFO
                            },
                            tag = "HttpClient",
                            throwable = null,
                            message = message,
                        )
                    }
                }
                level = if (isDebug) LogLevel.ALL else LogLevel.INFO
            }
            expectSuccess = true
        }
    }
}

