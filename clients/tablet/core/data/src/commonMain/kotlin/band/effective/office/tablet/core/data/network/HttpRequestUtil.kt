package band.effective.office.tablet.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Utility class for making HTTP requests and handling errors
 */
object HttpRequestUtil {
    /**
     * HTTP request methods
     */
    enum class Method { GET, POST, PUT, DELETE }

    /**
     * Result of an HTTP request
     */
    sealed class Result<out T> {
        /**
         * Successful result with data
         */
        data class Success<T>(val data: T) : Result<T>()

        /**
         * Error result with error code and message
         */
        data class Error(val code: Int, val message: String) : Result<Nothing>()
    }

    /**
     * Makes an HTTP request and returns the result
     * @param client HttpClient instance
     * @param url URL to request
     * @param method HTTP method to use
     * @param block Request configuration block
     * @return Result of the request
     */
    suspend inline fun <reified T> request(
        client: HttpClient = HttpClientProvider.create(),
        url: String,
        method: Method = Method.GET,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = when (method) {
                Method.GET -> client.get(url) { block() }
                Method.POST -> client.post(url) { block() }
                Method.PUT -> client.put(url) { block() }
                Method.DELETE -> client.delete(url) { block() }
            }

            if (response.status.value in 200..299) {
                Result.Success(response.body())
            } else {
                Result.Error(response.status.value, "HTTP error: ${response.status.value}")
            }
        } catch (e: Exception) {
            Result.Error(0, e.message ?: "Unknown error")
        }
    }
}
