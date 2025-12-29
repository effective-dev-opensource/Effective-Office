package band.effective.office.smsrouter.presentation.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.effective.office.smsrouter.domain.repository.SmsLogsRepository
import band.effective.office.smsrouter.presentation.model.SmsLog
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageScreenViewModel(
    private val smsLogsRepository: SmsLogsRepository
) : ViewModel() {

    val smsLogs: StateFlow<List<SmsLog>> = smsLogsRepository.state

    fun clearAllLogs() {
        viewModelScope.launch {
            smsLogsRepository.clearAllLogs()
        }
    }
}
