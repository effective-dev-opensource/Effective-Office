package band.effective.office.smsrouter.domain.repository

import band.effective.office.smsrouter.presentation.model.SmsLog
import kotlinx.coroutines.flow.StateFlow

interface SmsLogsRepository {
    val state: StateFlow<List<SmsLog>>

    fun put(log: SmsLog)

    suspend fun clearAllLogs()
}
