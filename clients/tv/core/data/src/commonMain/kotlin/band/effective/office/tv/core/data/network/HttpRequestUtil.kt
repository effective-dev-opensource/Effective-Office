package band.effective.office.tv.core.data.network

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.shared.core.network.HttpRequestUtil
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder

/**
 * Utility function for making GET requests.
 */
suspend inline fun <reified T> get(
    client: HttpClient,
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): Either<ErrorResponse, T> {
    return when (val result = HttpRequestUtil.request<T>(
        client = client,
        url = url,
        method = HttpRequestUtil.Method.GET,
        block = block
    )) {
        is HttpRequestUtil.Result.Success -> Either.Success(result.data)
        is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
    }
}