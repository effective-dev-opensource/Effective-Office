package band.effective.office.smsrouter.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SmsDataRequest(
    val text: String,
)