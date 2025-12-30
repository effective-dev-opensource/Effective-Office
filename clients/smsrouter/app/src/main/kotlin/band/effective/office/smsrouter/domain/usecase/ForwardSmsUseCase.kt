package band.effective.office.smsrouter.domain.usecase

import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse
import band.effective.office.smsrouter.domain.model.SmsData

interface ForwardSmsUseCase {
    suspend operator fun invoke(sms: SmsData): Either<ErrorResponse, Unit>
}