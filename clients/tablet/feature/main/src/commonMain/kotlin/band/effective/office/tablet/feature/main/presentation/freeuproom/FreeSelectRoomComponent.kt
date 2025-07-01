package band.effective.office.tablet.feature.main.presentation.freeuproom

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FreeSelectRoomComponent(
    componentContext: ComponentContext,
    private val eventInfo: EventInfo,
    private val onRemoveEvent: (EventInfo) -> Unit,
    private val onCloseRequest: () -> Unit,
) : ComponentContext by componentContext, ModalWindow {
    private val scope = componentCoroutineScope()

    private val mutableState = MutableStateFlow(State.defaultState)
    val state = mutableState.asStateFlow()

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
        onRemoveEvent(eventInfo)
        onCloseRequest()
        mutableState.update { State.defaultState }
    }
}