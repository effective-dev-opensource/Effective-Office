package band.effective.office.tablet.feature.main.presentation.freeuproom

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
import band.effective.office.tablet.feature.main.domain.FreeUpRoomUseCase
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FreeSelectRoomComponent(
    componentContext: ComponentContext,
    private val eventInfo: EventInfo,
    private val roomName: String,
    private val onCloseRequest: () -> Unit,
) : ComponentContext by componentContext, ModalWindow, KoinComponent {
    private val scope = componentCoroutineScope()

    private val mutableState = MutableStateFlow(State.defaultState)
    val state = mutableState.asStateFlow()

    private val freeUpRoomUseCase: FreeUpRoomUseCase by inject()

    fun sendIntent(intent: Intent) {
        when (intent) {
            Intent.OnCloseWindowRequest -> {
                onCloseRequest()
                mutableState.update { State.defaultState }
            }

            Intent.OnFreeSelectRequest -> freeRoom()
        }
    }

    private fun freeRoom() = scope.launch {
        mutableState.update { it.copy(isLoad = true) }
        freeUpRoomUseCase(
            roomName = roomName,
            eventInfo = eventInfo,
        )
        onCloseRequest()
        mutableState.update { State.defaultState }
    }
}