package band.effective.office.tablet.feature.main.presentation.freeuproom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.useCase.DeleteBookingUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FreeSelectRoomViewModel(
    private val deleteBookingUseCase: DeleteBookingUseCase,
    private val eventInfo: EventInfo,
    private val roomName: String,
) : ViewModel() {

    private val mutableState = MutableStateFlow(State.defaultState)
    val state = mutableState.asStateFlow()

    private val closeChannel = Channel<Unit>(Channel.BUFFERED)
    val closeEvents = closeChannel.receiveAsFlow()

    fun sendIntent(intent: Intent) {
        when (intent) {
            Intent.OnCloseWindowRequest -> {
                requestClose()
                mutableState.update { State.defaultState }
            }

            Intent.OnFreeSelectRequest -> freeRoom()
        }
    }

    private fun freeRoom() = viewModelScope.launch {
        mutableState.update { it.copy(isLoad = true) }
        deleteBookingUseCase(
            roomName = roomName,
            eventInfo = eventInfo,
        )
        requestClose()
        mutableState.update { State.defaultState }
    }

    private fun requestClose() {
        viewModelScope.launch { closeChannel.send(Unit) }
    }
}
