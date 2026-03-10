package band.effective.office.smsrouter.presentation.model

sealed class SmsStatus(
    val storageValue: String
) {
    data object Delivered : SmsStatus(storageValue = "DELIVERED")
    data object Error : SmsStatus(storageValue = "ERROR")
    data object InProgress : SmsStatus(storageValue = "IN_PROGRESS")

    companion object {
        fun fromStorageValue(value: String): SmsStatus {
            return when (value) {
                Delivered.storageValue -> Delivered
                Error.storageValue -> Error
                InProgress.storageValue -> InProgress
                else -> InProgress
            }
        }
    }
}
