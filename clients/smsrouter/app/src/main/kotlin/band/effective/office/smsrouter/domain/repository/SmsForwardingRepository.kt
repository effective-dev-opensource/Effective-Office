package band.effective.office.smsrouter.domain.repository

import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse
import band.effective.office.smsrouter.domain.model.SmsData
import band.effective.office.smsrouter.domain.model.WebhookType

interface SmsForwardingRepository {

    suspend fun forwardSms(
        url: String,
        secretKey: String,
        smsData: SmsData,
        webhookType: WebhookType = WebhookType.MATTERMOST,
        chatId: String = "",
        smsId: String = "", // Optional SMS ID for tracking retries
        onRetry: ((smsId: String, retryCount: Int) -> Unit)? = null // Callback for retry attempts
    ): Either<ErrorResponse, Unit>
}
