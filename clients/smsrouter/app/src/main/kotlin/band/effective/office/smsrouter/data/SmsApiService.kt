package band.effective.office.smsrouter.data

import band.effective.office.smsrouter.data.models.SmsDataRequest
import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse

interface SmsApiService {
    suspend fun sendSms(
        url: String,
        secretKey: String,
        body: SmsDataRequest,
        smsId: String = "", // Optional SMS ID for tracking retries
        onRetry: ((smsId: String, retryCount: Int) -> Unit)? = null // Callback for retry attempts
    ): Either<ErrorResponse, Unit>
}
