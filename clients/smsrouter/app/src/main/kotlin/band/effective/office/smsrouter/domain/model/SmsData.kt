package band.effective.office.smsrouter.domain.model

data class SmsData(
    val sender: String,
    val operatorName: String,
    val messageBody: String,
    val recipientPhoneNumber: String = ""
)
