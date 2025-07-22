package band.effective.office.smsrouter.data

import band.effective.office.smsrouter.data.models.SmsDataRequest
import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse

interface SmsApiService {
    suspend fun sendSms(
        url: String,
        secretKey: String,
        body: SmsDataRequest,
    ): Either<ErrorResponse, Unit>
}