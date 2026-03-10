package band.effective.office.smsrouter.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import band.effective.office.smsrouter.presentation.model.SmsLog
import band.effective.office.smsrouter.presentation.model.SmsStatus

@Entity(tableName = "sms_logs")
data class SmsLogEntity(
    @PrimaryKey
    val id: String,
    val sender: String,
    val message: String,
    val simType: String,
    val timestamp: Long,
    val status: String,
    val errorDetails: String?,
    val retryCount: Int = 0
) {
    companion object {
        fun fromSmsLog(smsLog: SmsLog): SmsLogEntity {
            return SmsLogEntity(
                id = smsLog.id,
                sender = smsLog.sender,
                message = smsLog.message,
                simType = smsLog.simType,
                timestamp = smsLog.timestamp,
                status = smsLog.status.storageValue,
                errorDetails = smsLog.errorDetails,
                retryCount = smsLog.retryCount
            )
        }

        fun toSmsLog(entity: SmsLogEntity): SmsLog {
            return SmsLog(
                id = entity.id,
                sender = entity.sender,
                message = entity.message,
                simType = entity.simType,
                timestamp = entity.timestamp,
                status = SmsStatus.fromStorageValue(entity.status),
                errorDetails = entity.errorDetails,
                retryCount = entity.retryCount
            )
        }
    }
}
