package band.effective.office.smsrouter.domain.usecase

import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse
import band.effective.office.smsrouter.domain.model.SmsData
import band.effective.office.smsrouter.domain.repository.SmsForwardingRepository

internal class ForwardSmsUseCaseImpl(
    private val smsForwardingRepository: SmsForwardingRepository,
) : ForwardSmsUseCase {
    override suspend fun invoke(sms: SmsData): Either<ErrorResponse, Unit> {
        return smsForwardingRepository.forwardSms(sms)
    }
}