package band.effective.office.smsrouter.presentation.model

data class SmsLog(
    val id: String,
    val sender: String,
    val message: String,
    val simType: String,
    val timestamp: Long,
    val status: SmsStatus = SmsStatus.IN_PROGRESS,
    val errorDetails: String? = null,
    val retryCount: Int = 0
)
