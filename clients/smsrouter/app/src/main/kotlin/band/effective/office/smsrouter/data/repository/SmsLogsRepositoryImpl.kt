package band.effective.office.smsrouter.data.repository

import band.effective.office.smsrouter.domain.repository.SmsLogsRepository
import band.effective.office.smsrouter.presentation.SmsLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class SmsLogsRepositoryImpl : SmsLogsRepository {
    private val mutableState = MutableStateFlow<List<SmsLog>>(emptyList())
    override val state: StateFlow<List<SmsLog>> = mutableState.asStateFlow()

    override fun put(log: SmsLog) = mutableState.update { it + log }
}