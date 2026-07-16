package band.effective.office.tablet.feature.main.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.effective.office.shared.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.manager.DateResetManager
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.DeleteBookingUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.shared.core.utils.minus
import band.effective.office.shared.core.utils.plus
import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import band.effective.office.tablet.feature.main.domain.GetRoomIndexUseCase
import band.effective.office.tablet.feature.main.domain.GetTimeToNextEventUseCase
import band.effective.office.tablet.feature.slot.presentation.SlotComponentFactory
import band.effective.office.tablet.feature.slot.presentation.SlotIntent
import kotlin.math.abs
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

/**
 * ViewModel responsible for managing room information, bookings, and navigation triggers.
 * Handles room selection, date selection, and requests to open modal windows.
 */
@OptIn(ExperimentalTime::class)
class MainViewModel(
    private val checkSettingsUseCase: CheckSettingsUseCase,
    private val roomInfoUseCase: RoomInfoUseCase,
    private val getRoomIndexUseCase: GetRoomIndexUseCase,
    private val getTimeToNextEventUseCase: GetTimeToNextEventUseCase,
    private val updateUseCase: UpdateUseCase,
    private val timerUseCase: TimerUseCase,
    private val deleteBookingUseCase: DeleteBookingUseCase,
    slotComponentFactory: SlotComponentFactory,
) : ViewModel() {

    private val coroutineScope = viewModelScope

    // Timers
    private val currentTimeTimer = BootstrapperTimer(timerUseCase, coroutineScope)

    // State management
    private val mutableState = MutableStateFlow(State.defaultState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    // One-time navigation requests, handled by the host NavController
    private val navEventChannel = Channel<MainNavEvent>(Channel.BUFFERED)
    val navEvents = navEventChannel.receiveAsFlow()

    // Child presenter for the room's time slots (built by the DI factory)
    val slotComponent = slotComponentFactory.create(
        coroutineScope = coroutineScope,
        roomName = ::getCurrentRoomName,
        openBookingDialog = ::openBookingDialog,
    )

    init {
        initializeComponent()
    }

    private fun emitNav(event: MainNavEvent) {
        coroutineScope.launch { navEventChannel.send(event) }
    }

    private fun openBookingDialog(event: EventInfo, room: String) {
        emitNav(MainNavEvent.OpenBookingEditor(event = event, room = room))
    }

    /**
     * Initializes the component, checking settings and setting up timers and event listeners.
     */
    private fun initializeComponent() {
        // Load initial room data
        loadRooms()

        // Set up event listeners
        setupEventListeners()

        // Initialize date reset manager
        initializeDateResetManager()
    }

    /**
     * Initializes the DateResetManager to handle date reset on inactivity.
     * This registers a callback that will reset the selected date and current room
     * when inactivity is detected.
     */
    private fun initializeDateResetManager() {
        DateResetManager.registerDateResetCallback { date ->
            mutableState.update { it.copy(selectedDate = date) }
            reboot(refresh = true, resetSelectRoom = true)
            updateTimeToNextEvent()
            slotComponent.sendIntent(SlotIntent.InactivityTimeout)
        }
    }

    /**
     * Sets up event listeners for updates and timers.
     */
    private fun setupEventListeners() {
        // Listen for room updates
        coroutineScope.launch(Dispatchers.IO) {
            updateUseCase.updateFlow().collect {
                delay(1.seconds)
                withContext(Dispatchers.Main) {
                    loadRooms(state.value.indexSelectRoom)
                }
            }
        }

        // Listen for room info changes
        coroutineScope.launch(Dispatchers.Main) {
            roomInfoUseCase.subscribe().collect { roomsInfo ->
                if (roomsInfo.isNotEmpty()) {
                    reboot(resetSelectRoom = false)
                }
            }
        }

        coroutineScope.launch {
            CurrentTimeHolder.currentTime.collect { updateTimeToNextEvent() }
        }
    }

    /**
     * Updates the time to the next event in the selected room.
     */
    private fun updateTimeToNextEvent() {
        mutableState.update {
            it.copy(
                timeToNextEvent = getTimeToNextEventUseCase(
                    state.value.roomList,
                    state.value.indexSelectRoom
                )
            )
        }
    }

    /**
     * Gets the name of the currently selected room.
     */
    private fun getCurrentRoomName(): String {
        return with(state.value) {
            if (roomList.isNotEmpty()) {
                roomList[indexSelectRoom].name
            } else {
                RoomInfo.defaultValue.name
            }
        }
    }

    /**
     * Handles intents from the UI.
     */
    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.OnFastBooking -> handleFastBookingIntent(intent)
            Intent.OnOpenFreeRoomModal -> handleFreeRoomIntent()
            is Intent.OnSelectRoom -> selectRoom(intent.index)
            is Intent.OnUpdateSelectDate -> updateSelectedDate(intent)
            Intent.RebootRequest -> reboot(refresh = true)
        }
    }

    /**
     * Handles the fast booking intent.
     */
    private fun handleFastBookingIntent(intent: Intent.OnFastBooking) {
        emitNav(MainNavEvent.OpenFastBooking(minDuration = intent.minDuration))
    }

    /**
     * Handles the free room intent.
     */
    private fun handleFreeRoomIntent() {
        val currentState = state.value
        val currentEvent = currentState.roomList[currentState.indexSelectRoom].currentEvent

        if (currentEvent != null) {
            emitNav(MainNavEvent.OpenFreeRoom(event = currentEvent, roomName = getCurrentRoomName()))
        }
    }

    /**
     * Updates the selected date.
     */
    private fun updateSelectedDate(intent: Intent.OnUpdateSelectDate) {
        currentTimeTimer.restart()

        val selectedDate = state.value.selectedDate
        val newDate = calculateNewDate(selectedDate, intent.updateInDays)

        // Only update if the new date is not in the past
        if (newDate.date >= currentLocalDateTime.date) {
            mutableState.update { it.copy(selectedDate = newDate) }
            val selectedRoom = state.value.roomList[state.value.indexSelectRoom]
            slotComponent.sendIntent(SlotIntent.UpdateRequest(selectedRoom.name, state.value.selectedDate))
        }
    }

    /**
     * Calculates a new date based on the current date and days to add.
     */
    private fun calculateNewDate(currentDate: LocalDateTime, daysToAdd: Int): LocalDateTime {
        return if (daysToAdd < 0) {
            currentDate.minus(abs(daysToAdd).days)
        } else {
            currentDate.plus(daysToAdd.days)
        }
    }

    /**
     * Selects a room by index.
     */
    private fun selectRoom(index: Int) {
        mutableState.update {
            it.copy(
                indexSelectRoom = index,
                timeToNextEvent = getTimeToNextEventUseCase(
                    rooms = state.value.roomList,
                    selectedRoomIndex = index,
                )
            )
        }

        val selectedRoom = state.value.roomList.getOrNull(index)
        if (selectedRoom != null) {
            slotComponent.sendIntent(SlotIntent.UpdateRequest(room = selectedRoom.name, state.value.selectedDate))
        }
    }

    /**
     * Data class to hold the result of loading rooms.
     */
    private data class RoomsResult(
        val isSuccess: Boolean,
        val roomList: List<RoomInfo>,
        val indexSelectRoom: Int,
    )

    /**
     * Loads room information.
     */
    private fun loadRooms(roomIndex: Int? = null) = coroutineScope.launch {
        val result = roomInfoUseCase()
        val roomsResult = processRoomInfoResult(result, roomIndex)

        updateStateWithRoomsResult(roomsResult)
    }

    /**
     * Processes the result of loading room information.
     */
    private fun processRoomInfoResult(
        result: Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>,
        roomIndex: Int? = null,
    ): RoomsResult {
        return when (result) {
            is Either.Error<ErrorWithData<List<RoomInfo>>> -> RoomsResult(
                isSuccess = false,
                roomList = result.error.saveData ?: listOf(RoomInfo.defaultValue),
                indexSelectRoom = 0
            )

            is Either.Success<List<RoomInfo>> -> {
                val roomIndex = roomIndex ?: result.data.indexOfFirst { it.name == checkSettingsUseCase() }
                RoomsResult(
                    isSuccess = true,
                    roomList = result.data,
                    indexSelectRoom = roomIndex,
                )
            }
        }
    }

    /**
     * Updates the state with room information.
     */
    private fun updateStateWithRoomsResult(roomsResult: RoomsResult) {
        mutableState.update {
            if (roomsResult.roomList.isEmpty()) {
                it.copy(
                    isLoad = false,
                    isData = false,
                    isError = true,
                    roomList = listOf(RoomInfo.defaultValue),
                    indexSelectRoom = 0,
                    timeToNextEvent = 0
                )
            } else {
                val selectedRoom = roomsResult.roomList[roomsResult.indexSelectRoom.coerceIn(0, roomsResult.roomList.size - 1)]
                slotComponent.sendIntent(SlotIntent.UpdateRequest(selectedRoom.name, state.value.selectedDate))
                it.copy(
                    isLoad = false,
                    isData = roomsResult.isSuccess,
                    isError = !roomsResult.isSuccess,
                    roomList = roomsResult.roomList,
                    indexSelectRoom = roomsResult.indexSelectRoom,
                    timeToNextEvent = getTimeToNextEventUseCase(
                        rooms = roomsResult.roomList,
                        selectedRoomIndex = roomsResult.indexSelectRoom
                    )
                )
            }
        }
    }

    /**
     * Reboots the component, optionally refreshing data and resetting the selected room.
     */
    private fun reboot(
        refresh: Boolean = false,
        resetSelectRoom: Boolean = true
    ) = coroutineScope.launch {
        val currentState = state.value
        val roomIndex = if (resetSelectRoom) {
            getRoomIndexUseCase(currentState.roomList)
        } else {
            currentState.indexSelectRoom
        }

        if (refresh && !currentState.isData) {
            prepareForRefresh(roomIndex)
            roomInfoUseCase.updateCache()
        }

        loadRooms(roomIndex)

        currentState.roomList.getOrNull(roomIndex)?.let { roomInfo ->
            slotComponent.sendIntent(SlotIntent.UpdateRequest(roomInfo.name, currentState.selectedDate))
        }
    }

    /**
     * Prepares the state for a refresh operation.
     */
    private fun prepareForRefresh(roomIndex: Int) {
        mutableState.update {
            it.copy(
                isError = false,
                isLoad = true,
                indexSelectRoom = roomIndex,
                timeToNextEvent = getTimeToNextEventUseCase(
                    rooms = state.value.roomList,
                    selectedRoomIndex = state.value.indexSelectRoom,
                )
            )
        }
    }
}

/** One-time navigation requests emitted by [MainViewModel], handled by the host NavController. */
sealed interface MainNavEvent {
    data class OpenFastBooking(val minDuration: Int) : MainNavEvent

    data class OpenFreeRoom(val event: EventInfo, val roomName: String) : MainNavEvent

    data class OpenBookingEditor(val event: EventInfo, val room: String) : MainNavEvent
}
