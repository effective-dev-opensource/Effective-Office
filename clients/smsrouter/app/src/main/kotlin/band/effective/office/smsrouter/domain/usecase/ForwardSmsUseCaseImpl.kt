package band.effective.office.smsrouter.domain.usecase

import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse
import band.effective.office.smsrouter.domain.model.SmsData
import band.effective.office.smsrouter.domain.repository.SettingsRepository
import band.effective.office.smsrouter.domain.repository.SmsForwardingRepository

internal class ForwardSmsUseCaseImpl(
    private val smsForwardingRepository: SmsForwardingRepository,
    private val settingsRepository: SettingsRepository,
) : ForwardSmsUseCase {
    override suspend fun invoke(sms: SmsData): Either<ErrorResponse, Unit> {
        val simId = sms.simId
        val webhookUrl = settingsRepository.getWebhookUrl(simId).orEmpty()
        val secretKey = settingsRepository.getSecretKey(simId).orEmpty()

        return smsForwardingRepository.forwardSms(
            url = webhookUrl,
            secretKey = secretKey,
            smsData = sms,
        )
    }
}