package band.effective.office.smsrouter.data

import band.effective.office.smsrouter.data.models.SmsDataRequest
import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse

interface SmsApiService {
    suspend fun sendSms(request: SmsDataRequest): Either<ErrorResponse, Unit>
}