package band.effective.office.smsrouter.data.mapper

import band.effective.office.smsrouter.data.models.SmsDataRequest
import band.effective.office.smsrouter.domain.model.SmsData

internal class SmsDataDtoMapper {

    fun map(domain: SmsData): SmsDataRequest = SmsDataRequest(
        text = domain.messageBody,
    )
}