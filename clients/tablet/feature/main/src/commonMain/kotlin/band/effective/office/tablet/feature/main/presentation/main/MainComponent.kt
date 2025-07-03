package band.effective.office.tablet.feature.main.presentation.main

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.tablet.core.domain.util.currentLocalDate
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.domain.util.minus
import band.effective.office.tablet.core.domain.util.plus
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
import band.effective.office.tablet.core.domain.useCase.DeleteBookingUseCase
import band.effective.office.tablet.feature.main.domain.FreeUpRoomUseCase
import band.effective.office.tablet.feature.main.domain.GetRoomIndexUseCase
import band.effective.office.tablet.feature.main.domain.GetTimeToNextEventUseCase
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomComponent
import band.effective.office.tablet.feature.main.presentation.main.navigation.ModalWindowsConfig
import band.effective.office.tablet.feature.slot.presentation.SlotComponent
import band.effective.office.tablet.feature.slot.presentation.SlotIntent
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorComponent
import band.effective.office.tablet.feature.fastBooking.presentation.FastBookingComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import kotlin.math.abs
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalTime::class)
class MainComponent(
    private val componentContext: ComponentContext,
    val onSettings: () -> Unit
) : ComponentContext by componentContext, KoinComponent {

    private val coroutineScope = componentCoroutineScope()

    // region: Dependencies
    private val checkSettingsUseCase: CheckSettingsUseCase by inject()
    private val roomInfoUseCase: RoomInfoUseCase by inject()
    private val getRoomIndexUseCase: GetRoomIndexUseCase by inject()
    private val getTimeToNextEventUseCase: GetTimeToNextEventUseCase by inject()
    private val updateUseCase: UpdateUseCase by inject()
    private val timerUseCase: TimerUseCase by inject()
    private val freeUpRoomUseCase: FreeUpRoomUseCase by inject()
    private val deleteBookingUseCase: DeleteBookingUseCase by inject()
    private val currentTimeTimer = BootstrapperTimer(timerUseCase, coroutineScope)
    private val currentRoomTimer = BootstrapperTimer(timerUseCase, coroutineScope)
    private val errorTimer = BootstrapperTimer(timerUseCase, coroutineScope)
    // endregion: Dependencies

    private val mutableState = MutableStateFlow(State.defaultState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val mutableLabel = MutableSharedFlow<Label>()
    val label: SharedFlow<Label> = mutableLabel.asSharedFlow()

    val slotComponent = SlotComponent(
        componentContext = componentContext,
        roomName = {
            with(state.value) {
                if (roomList.isNotEmpty())
                    roomList[indexSelectRoom].name
                else RoomInfo.defaultValue.name
            }
        },
        openBookingDialog = { event, room ->
            navigation.activate(
                ModalWindowsConfig.UpdateEvent(
                    event = event,
                    room = room,
                )
            )
        }
    )
    private val navigation = SlotNavigation<ModalWindowsConfig>()
    val modalWindowSlot = childSlot(
        source = navigation,
        childFactory = ::childFactory,
        serializer = ModalWindowsConfig.serializer(),
    )

    init {
        if (checkSettingsUseCase().isEmpty()) onSettings()
        loadRooms()
        // update events when start/finish event in room
        coroutineScope.launch(Dispatchers.IO) {
            updateUseCase.updateFlow().collect {
                delay(1.seconds)
                withContext(Dispatchers.Main) {
                    loadRooms()
                }
            }
        }
        // update time to start next event or finish current event
        timerUseCase.timer(coroutineScope, 1.seconds) { _ ->
            withContext(Dispatchers.Main) {
                mutableState.update {
                    it.copy(
                        timeToNextEvent = getTimeToNextEventUseCase(
                            state.value.roomList,
                            state.value.indexSelectRoom
                        )
                    )
                }
            }
        }
        // update events list
        CoroutineScope(Dispatchers.Main).launch() {
            roomInfoUseCase.subscribe().collect { roomsInfo ->
                if (roomsInfo.isNotEmpty())
                    reboot(resetSelectRoom = false)
            }
        }
        // reset selected room
        currentRoomTimer.start(delay = 1.minutes) {
            withContext(Dispatchers.Main) {
                loadRooms()
            }
        }
        // update cache when get error
        errorTimer.init(15.minutes) {
            roomInfoUseCase.updateCache()
            withContext(Dispatchers.Main) {
                loadRooms()
            }
        }
        // reset select date
        currentTimeTimer.start(1.minutes) {
            withContext(Dispatchers.Main) {
                mutableState.update {
                    it.copy(
                        selectedDate = currentLocalDateTime,
                        currentDate = currentLocalDateTime,
                    )
                }
                slotComponent.sendIntent(SlotIntent.UpdateDate(currentLocalDate))
            }
        }
    }

    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.OnFastBooking ->
                navigation.activate(
                    ModalWindowsConfig.FastEvent(
                        minEventDuration = intent.minDuration,
                        selectedRoom = state.value.run { roomList[indexSelectRoom] },
                        rooms = state.value.run { roomList }
                    )
                )

            Intent.OnOpenFreeRoomModal -> navigation.activate(
                ModalWindowsConfig.FreeRoom(
                    state.value.roomList[state.value.indexSelectRoom].currentEvent!!
                )
            )

            is Intent.OnSelectRoom -> selectRoom(intent.index)
            is Intent.OnUpdateSelectDate -> updateSelectDate(intent)
            Intent.RebootRequest -> reboot(refresh = true)
        }
    }

    private fun childFactory(
        modalWindows: ModalWindowsConfig,
        componentContext: ComponentContext
    ): ModalWindow {
        return when (modalWindows) {
            is ModalWindowsConfig.FreeRoom -> FreeSelectRoomComponent(
                componentContext = componentContext,
                eventInfo = modalWindows.event,
                onRemoveEvent = { event ->
                    coroutineScope.launch {
                        freeUpRoomUseCase(
                            roomName = state.value.run { roomList[indexSelectRoom].name },
                            eventInfo = event
                        )
                    }
                },
                onCloseRequest = navigation::dismiss,
            )

            is ModalWindowsConfig.UpdateEvent -> BookingEditorComponent(
                componentContext = componentContext,
                event = modalWindows.event,
                room = state.value.run { roomList[indexSelectRoom].name },

                onDelete = { slot ->
                    slotComponent.sendIntent(
                        SlotIntent.Delete(
                            slot = slot,
                            onDelete = {
                                this.componentContext.componentCoroutineScope().launch {
                                    (slot as? Slot.EventSlot)?.eventInfo?.apply {
                                        deleteBookingUseCase(
                                            eventInfo = this,
                                            roomName = state.value.run { roomList[indexSelectRoom].name }
                                        )
                                    }
                                }
                            }
                        )
                    )
                },
                onCloseRequest = navigation::dismiss,
            )

            is ModalWindowsConfig.FastEvent -> FastBookingComponent(
                componentContext = componentContext,
                minEventDuration = modalWindows.minEventDuration,
                selectedRoom = modalWindows.selectedRoom,
                rooms = modalWindows.rooms,
                onCloseRequest = navigation::dismiss,
            )
        }
    }

    private fun updateSelectDate(intent: Intent.OnUpdateSelectDate) {
        currentTimeTimer.restart()
        currentRoomTimer.restart()
        val selectedDate = state.value.selectedDate
        val newDate = if (intent.updateInDays < 0) {
            selectedDate.minus(abs(intent.updateInDays).days)
        } else {
            selectedDate.plus(intent.updateInDays.days)
        }

        // there is no point in looking at bookings from the past days
        if (newDate.date >= currentLocalDateTime.date) {
            mutableState.update { it.copy(selectedDate = newDate) }
            slotComponent.sendIntent(SlotIntent.UpdateDate(newDate.date))
        }
    }

    private fun selectRoom(index: Int) {
        currentRoomTimer.restart()
        mutableState.update {
            it.copy(
                indexSelectRoom = index,
                timeToNextEvent = getTimeToNextEventUseCase(
                    rooms = state.value.roomList,
                    selectedRoomIndex = index,
                )
            )
        }
        updateComponents(state.value.roomList[index], state.value.selectedDate.date)
    }

    private fun updateComponents(roomInfo: RoomInfo, date: LocalDate) {
        slotComponent.sendIntent(
            SlotIntent.UpdateRequest(
                room = roomInfo.name,
                refresh = false
            )
        )
        slotComponent.sendIntent(
            SlotIntent.UpdateDate(date)
        )
    }

    private fun loadRooms() = coroutineScope.launch {

        data class RoomsResult(
            val isSuccess: Boolean,
            val roomList: List<RoomInfo>,
            val indexSelectRoom: Int,
        )

        val result = roomInfoUseCase()

        val (isSuccess, roomList, indexSelectRoom) = when (result) {
            is Either.Error -> RoomsResult(
                isSuccess = false,
                roomList = result.error.saveData ?: listOf(RoomInfo.defaultValue),
                indexSelectRoom = 0
            )

            is Either.Success -> RoomsResult(
                isSuccess = true,
                roomList = result.data,
                indexSelectRoom = getRoomIndexUseCase(state.value.roomList),
            )
        }
        mutableState.update {
            it.copy(
                isLoad = false,
                isData = isSuccess,
                isError = !isSuccess,
                roomList = roomList,
                indexSelectRoom = indexSelectRoom,
                timeToNextEvent = getTimeToNextEventUseCase(
                    state.value.roomList,
                    state.value.indexSelectRoom
                ),
            )
        }
    }

    private fun reboot(
        refresh: Boolean = false,
        resetSelectRoom: Boolean = true
    ) = coroutineScope.launch {
        val state = state.value
        val roomIndex = if (resetSelectRoom) getRoomIndexUseCase(state.roomList) else state.indexSelectRoom
        if (refresh) {
            if (!state.isData) {
                mutableState.update {
                    it.copy(
                        isError = false,
                        isLoad = true,
                        indexSelectRoom = roomIndex,
                        timeToNextEvent = getTimeToNextEventUseCase(
                            rooms = state.roomList,
                            selectedRoomIndex = state.indexSelectRoom,
                        )
                    )
                }
            }
            roomInfoUseCase.updateCache()
        }
        loadRooms()
        state.roomList.getOrNull(roomIndex)?.let { roomInfo ->
            updateComponents(roomInfo, state.selectedDate.date)
        }
    }
}
