package band.effective.office.smsrouter.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SmsDataRequest(
    val sender: String,
    val operatorName: String,
    val messageBody: String,
    val recipientPhoneNumber: String = ""
)