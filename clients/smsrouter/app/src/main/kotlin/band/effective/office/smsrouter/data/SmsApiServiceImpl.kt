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
        val result = HttpRequestUtil.request<Unit>(
            client = client,
            url = url,
            method = HttpRequestUtil.Method.POST,
        ) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $secretKey")
            setBody(body)
        }
        return when (result) {
            is HttpRequestUtil.Result.Success -> Either.Success(Unit)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }
}
