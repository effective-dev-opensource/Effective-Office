package band.effective.office.smsrouter.data

import band.effective.office.base.data.network.HttpRequestUtil
import band.effective.office.smsrouter.data.models.SmsDataRequest
import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class SmsApiServiceImpl(
    private val client: HttpClient,
) : SmsApiService {

    override suspend fun sendSms(
        url: String,
        secretKey: String,
        body: SmsDataRequest,
    ): Either<ErrorResponse, Unit> {
        var lastResult: HttpRequestUtil.Result<Unit>? = null
        val maxAttempts = 3
        val timeoutMillis = 60000L // 1 minute timeout for the entire process
        val startTime = System.currentTimeMillis()

        for (attempt in 1..maxAttempts) {
            // Check if we've exceeded the timeout
            if (System.currentTimeMillis() - startTime >= timeoutMillis) {
                break // Exit the retry loop if we've exceeded the timeout
            }

            lastResult = HttpRequestUtil.request<Unit>(
                client = client,
                url = url,
                method = HttpRequestUtil.Method.POST,
            ) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $secretKey")
                setBody(body)
            }

            when (lastResult) {
                is HttpRequestUtil.Result.Success -> return Either.Success(Unit)
                is HttpRequestUtil.Result.Error -> {
                    if (attempt < maxAttempts) {
                        val elapsedTime = System.currentTimeMillis() - startTime
                        val remainingTime = timeoutMillis - elapsedTime
                        if (remainingTime <= 0) break // Exit the retry loop if no time remains
                    }
                }
            }
        }

        // If we get here, all attempts failed or timeout occurred
        val errorResult = lastResult as? HttpRequestUtil.Result.Error
            ?: return Either.Error(ErrorResponse(408, "Request timeout after $timeoutMillis ms"))

        return Either.Error(ErrorResponse(errorResult.code, errorResult.message))
    }
}
