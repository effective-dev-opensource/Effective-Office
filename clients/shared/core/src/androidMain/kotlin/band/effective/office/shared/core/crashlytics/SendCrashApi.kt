package band.effective.office.shared.core.crashlytics

import band.effective.office.shared.core.network.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType

class SendCrashApi(
    private val botToken: String,
    private val chatId: String,
    private val client: HttpClient = HttpClientFactory.createHttpClient()
) {
    suspend fun send(
        stacktrace: String,
        source: String,
    ) = runCatching {
        val stacktraceParts = stacktrace.chunked(3000)
        sendMessageToChat(
            """Detected crush in $source
                    ${stacktraceParts[0]}"""
        )
        stacktraceParts.drop(1).forEach { sendMessageToChat(it) }
    }

    private suspend fun sendMessageToChat(message: String) {
        client.post("https://api.telegram.org/bot$botToken/sendMessage") {
            contentType(Application.Json)
            setBody(
                """
                {
                    "chat_id": $chatId, 
                    "text": "$message"
                }
            """.trimIndent()
            )
        }
    }
}