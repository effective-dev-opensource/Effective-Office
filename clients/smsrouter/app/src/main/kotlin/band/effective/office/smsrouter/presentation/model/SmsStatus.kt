package band.effective.office.smsrouter.presentation.model

enum class SmsStatus {
    DELIVERED,
    ERROR,
    IN_PROGRESS;

    companion object {
        fun fromStorageValue(value: String): SmsStatus {
            return entries.firstOrNull { it.name == value } ?: IN_PROGRESS
        }
    }
}
