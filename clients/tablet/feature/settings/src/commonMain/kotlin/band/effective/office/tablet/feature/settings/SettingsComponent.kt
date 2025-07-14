package band.effective.office.tablet.feature.settings

import band.effective.office.tablet.core.domain.unbox
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.SetRoomUseCase
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsComponent(
    private val onExitApp: () -> Unit,
    private val onMainScreen: () -> Unit,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, KoinComponent {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val setRoomUseCase: SetRoomUseCase by inject()
    private val checkSettingsUseCase: CheckSettingsUseCase by inject()
    private val roomUseCase: RoomInfoUseCase by inject()

    private val mutableState = MutableStateFlow(State.defaultState)
    val state = mutableState.asStateFlow()

    init {
        coroutineScope.launch {
            setCurrentRoom(checkSettingsUseCase())
        }
        coroutineScope.launch {
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

    private fun setCurrentRoom(checkSettingsUseCase: String) {
        mutableState.update { it.copy(currentName = checkSettingsUseCase) }
    }

    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.ChangeCurrentNameRoom -> {
                setRoomUseCase(intent.nameRoom)
                onMainScreen()
            }

            Intent.OnExitApp -> onExitApp()
            Intent.SaveData -> onMainScreen()
        }
    }
}
