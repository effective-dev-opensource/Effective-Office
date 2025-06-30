package band.effective.office.tablet.feature.main

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.ui.common.ModalWindow
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalTime::class)
class MainComponent(
    private val componentContext: ComponentContext,
    val onSettings: () -> Unit
) : ComponentContext by componentContext, KoinComponent {

    private val checkSettingsUseCase: CheckSettingsUseCase by inject()

    private val mutableState = MutableStateFlow(State.defaultState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val mutableLabel = MutableSharedFlow<Label>()
    val label: SharedFlow<Label> = mutableLabel.asSharedFlow()

    private val navigation = SlotNavigation<ModalWindowsConfig>()

    val modalWindowSlot = childSlot(
        source = navigation,
        childFactory = ::childFactory,
        serializer = ModalWindowsConfig.serializer(),
    )

    init {
        getCurrentRoom()
    }

    private fun getCurrentRoom() {
        if (checkSettingsUseCase().isEmpty()) {
            onSettings()
        }
    }

    // Intent handling
    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.OnChangeEventRequest -> TODO()
            is Intent.OnFastBooking -> TODO()
            Intent.OnOpenFreeRoomModal -> TODO()
            Intent.OnResetSelectDate -> TODO()
            is Intent.OnSelectRoom -> TODO()
            Intent.OnUpdate -> TODO()
            is Intent.OnUpdateSelectDate -> TODO()
            Intent.RebootRequest -> TODO()
        }
    }

    private fun childFactory(
        modalWindows: ModalWindowsConfig,
        componentContext: ComponentContext
    ): ModalWindow {
        return object : ModalWindow {}
    }

    @Serializable
    sealed interface ModalWindowsConfig {
        @Serializable
        data class UpdateEvent(
            val event: EventInfo,
            val room: String
        ) : ModalWindowsConfig

        @Serializable
        data class FreeRoom(val event: EventInfo) : ModalWindowsConfig

        @Serializable
        data class FastEvent(
            val minEventDuration: Int,
            val selectedRoom: RoomInfo,
            val rooms: List<RoomInfo>
        ) : ModalWindowsConfig
    }
}
