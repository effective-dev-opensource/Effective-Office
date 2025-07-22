package band.effective.office.smsrouter.domain.repository

import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse
import band.effective.office.smsrouter.domain.model.SmsData

interface SmsForwardingRepository {

    suspend fun forwardSms(
        url: String,
        secretKey: String,
        smsData: SmsData,
    ): Either<ErrorResponse, Unit>
}