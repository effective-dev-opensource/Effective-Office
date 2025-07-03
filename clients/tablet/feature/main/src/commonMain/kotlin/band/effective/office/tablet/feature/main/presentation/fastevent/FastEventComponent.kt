package band.effective.office.tablet.feature.main.presentation.fastevent

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.useCase.SelectRoomUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import band.effective.office.tablet.core.domain.util.cropSeconds
import band.effective.office.tablet.core.domain.util.currentInstant
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
import band.effective.office.tablet.feature.main.domain.CreateBookingUseCase
import band.effective.office.tablet.feature.main.domain.DeleteBookingUseCase
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FastEventComponent(
    private val componentContext: ComponentContext,
    val minEventDuration: Int,
    val selectedRoom: RoomInfo,
    val rooms: List<RoomInfo>,
    private val onCloseRequest: () -> Unit
) : ComponentContext by componentContext, KoinComponent, ModalWindow {

    private val scope = componentCoroutineScope()

    val selectRoomUseCase: SelectRoomUseCase by inject()
    private val createFastBookingUseCase: CreateBookingUseCase by inject()
    private val deleteBookingUseCase: DeleteBookingUseCase by inject()
    private val timerUseCase: TimerUseCase by inject()
    private val currentTimeTimer = BootstrapperTimer(timerUseCase, scope)

    private val mutableState = MutableStateFlow(State.defaultState)
    val state = mutableState.asStateFlow()

    private val navigation = StackNavigation<ModalConfig>()

    val childStack = childStack(
        source = navigation,
        initialConfiguration = ModalConfig.LoadingModal,
        serializer = ModalConfig.serializer(),
        childFactory = { config, _ -> config },
    )

    init {
        scope.launch {
            val selectRoom: RoomInfo? = selectRoomUseCase.getRoom(
                currentRoom = selectedRoom,
                rooms = rooms,
                minEventDuration = minEventDuration
            )

            if (selectRoom != null) {
                createEvent(selectRoom.name, minEventDuration)
                return@launch
            }

            mutableState.update { it.copy(isLoad = false, isSuccess = false) }
            val nearestFreeRoom = selectRoomUseCase.getNearestFreeRoom(rooms, minEventDuration)

            mutableState.update { it.copy(minutesLeft = nearestFreeRoom.second.inWholeMinutes.toInt()) }
            navigation.push(ModalConfig.FailureModal(nearestFreeRoom.first.name))
        }
        mutableState.update { it.copy(currentTime = currentLocalDateTime) }

        currentTimeTimer.start(1.minutes) {
            withContext(Dispatchers.Main) {
                mutableState.update { it.copy(currentTime = currentLocalDateTime) }
            }
        }
    }

    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.OnFreeSelectRequest -> freeRoom(intent.room)

            Intent.OnCloseWindowRequest -> onCloseRequest()
        }
    }

    private fun createEvent(room: String, minDuration: Int) = scope.launch {
        val eventInfo = EventInfo.emptyEvent.copy(
            startTime = currentLocalDateTime.cropSeconds(),
            finishTime = currentInstant.plus(minDuration.minutes).asLocalDateTime.cropSeconds()
        )
        when (val result = createFastBookingUseCase(room, eventInfo)) {
            is Either.Success -> {
                mutableState.update {
                    it.copy(
                        event = eventInfo.copy(id = result.data.id),
                        isLoad = false,
                        isSuccess = true,
                    )
                }
                navigation.push(ModalConfig.SuccessModal(room, eventInfo))
            }

            is Either.Error -> {
                mutableState.update { it.copy(isSuccess = false) }
                navigation.push(ModalConfig.FailureModal(room))
            }
        }
    }

    private fun freeRoom(room: String) = scope.launch {
        mutableState.update { it.copy(isLoad = true) }
        deleteBookingUseCase(room, state.value.event)
        onCloseRequest()
    }

    @Serializable
    sealed interface ModalConfig {
        @Serializable
        data class SuccessModal(val room: String, val eventInfo: EventInfo) : ModalConfig

        @Serializable
        data class FailureModal(val room: String) : ModalConfig

        @Serializable
        object LoadingModal : ModalConfig
    }
}