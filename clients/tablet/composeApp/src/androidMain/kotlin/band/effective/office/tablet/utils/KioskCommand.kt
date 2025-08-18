package band.effective.office.tablet.utils

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface KioskCommand {
    object Enable : KioskCommand
    object Disable : KioskCommand
}

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

class KioskLifecycleObserver(
    private val activity: Activity,
    private val kioskManager: KioskManager,
    private val kioskCommandBus: KioskCommandBus,
    private val coroutineScope: CoroutineScope
) : DefaultLifecycleObserver {

    private var job: Job? = null

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        job = kioskCommandBus.commandFlow
            .onEach { command ->
                when (command) {
                    KioskCommand.Enable -> kioskManager.enableKioskMode(activity)
                    KioskCommand.Disable -> kioskManager.disableKioskMode(activity)
                }
            }
            .launchIn(coroutineScope)
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }
}