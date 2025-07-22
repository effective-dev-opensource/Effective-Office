package band.effective.office.smsrouter.data.mapper

import band.effective.office.smsrouter.data.models.SmsDataRequest
import band.effective.office.smsrouter.domain.model.SmsData

internal class SmsDataDtoMapper {

    fun map(domain: SmsData): SmsDataRequest = SmsDataRequest(
        sender = domain.sender,
        operatorName = domain.operatorName,
        messageBody = domain.messageBody,
        recipientPhoneNumber = domain.recipientPhoneNumber,
    )
}