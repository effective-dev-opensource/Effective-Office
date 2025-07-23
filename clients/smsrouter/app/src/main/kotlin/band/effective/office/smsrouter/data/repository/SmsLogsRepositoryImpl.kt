package band.effective.office.smsrouter.data.repository

import band.effective.office.smsrouter.data.database.SmsLogDao
import band.effective.office.smsrouter.data.database.SmsLogEntity
import band.effective.office.smsrouter.domain.repository.SmsLogsRepository
import band.effective.office.smsrouter.presentation.SmsLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SmsLogsRepositoryImpl(
    private val smsLogDao: SmsLogDao
) : SmsLogsRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override val state: StateFlow<List<SmsLog>> = smsLogDao.getAllLogs()
        .map { entities -> entities.map { SmsLogEntity.toSmsLog(it) } }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override fun put(log: SmsLog) {
        coroutineScope.launch {
            smsLogDao.insertLog(SmsLogEntity.fromSmsLog(log))
        }
    }

    override suspend fun clearAllLogs() {
        smsLogDao.deleteAllLogs()
    }
}
