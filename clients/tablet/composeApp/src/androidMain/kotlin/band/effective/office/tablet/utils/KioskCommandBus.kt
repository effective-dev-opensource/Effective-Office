package band.effective.office.tablet.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class KioskCommandBus {
    private val _commandFlow = MutableSharedFlow<KioskCommand>()
    val commandFlow = _commandFlow.asSharedFlow()

    fun sendCommand(command: KioskCommand, scope: CoroutineScope) {
        scope.launch {
            _commandFlow.emit(command)
        }
    }

    companion object {
        private var instance: KioskCommandBus? = null

        fun getInstance(): KioskCommandBus {
            return instance ?: synchronized(this) {
                instance ?: KioskCommandBus().also { instance = it }
            }
        }
    }
}