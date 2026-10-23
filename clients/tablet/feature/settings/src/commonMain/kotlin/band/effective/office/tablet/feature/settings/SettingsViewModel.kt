package band.effective.office.tablet.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.effective.office.shared.core.domain.unbox
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.SetRoomUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val setRoomUseCase: SetRoomUseCase,
    private val checkSettingsUseCase: CheckSettingsUseCase,
    private val roomUseCase: RoomInfoUseCase,
) : ViewModel() {

    private val mutableState = MutableStateFlow(State.defaultState)
    val state = mutableState.asStateFlow()

    private val navEventChannel = Channel<SettingsNavEvent>(Channel.BUFFERED)
    val navEvents = navEventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            setCurrentRoom(checkSettingsUseCase())
        }
        viewModelScope.launch {
            mutableState.update { it.copy(loading = true) }
            roomUseCase.updateCache().unbox(
                errorHandler = { error ->
                    mutableState.update { it.copy(error = error.error.description, loading = false) }
                }
            )
            val rooms = roomUseCase.getRoomsNames()
            mutableState.update { it.copy(rooms = rooms, loading = false) }
        }
    }

    private fun setCurrentRoom(currentRoom: String) {
        mutableState.update { it.copy(currentName = currentRoom) }
    }

    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.ChangeCurrentNameRoom -> {
                setRoomUseCase(intent.nameRoom)
                emitNav(SettingsNavEvent.NavigateToMain)
            }

            Intent.OnExitApp -> emitNav(SettingsNavEvent.ExitApp)
            Intent.SaveData -> emitNav(SettingsNavEvent.NavigateToMain)
        }
    }

    private fun emitNav(event: SettingsNavEvent) {
        viewModelScope.launch { navEventChannel.send(event) }
    }
}

/** One-time navigation requests emitted by [SettingsViewModel], handled by the host NavController. */
sealed interface SettingsNavEvent {
    data object NavigateToMain : SettingsNavEvent
    data object ExitApp : SettingsNavEvent
}
