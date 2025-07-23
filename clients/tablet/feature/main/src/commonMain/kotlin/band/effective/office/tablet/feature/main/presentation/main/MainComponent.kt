package band.effective.office.tablet.feature.main.presentation.main

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.DeleteBookingUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.tablet.core.domain.util.ComponentLoggable
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.domain.util.minus
import band.effective.office.tablet.core.domain.util.plus
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
import band.effective.office.tablet.feature.main.domain.GetRoomIndexUseCase
import band.effective.office.tablet.feature.main.domain.GetTimeToNextEventUseCase
import band.effective.office.tablet.feature.slot.presentation.SlotComponent
import band.effective.office.tablet.feature.slot.presentation.SlotIntent
import com.arkivanov.decompose.ComponentContext
import kotlin.math.abs
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
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
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Main component responsible for managing room information, bookings, and navigation.
 * Handles room selection, date selection, and modal windows for booking and room management.
 */
@OptIn(ExperimentalTime::class)
class MainComponent(
    private val componentContext: ComponentContext,
    val onFastBooking: (minDuration: Int, selectedRoom: RoomInfo, rooms: List<RoomInfo>) -> Unit,
    val onOpenFreeRoomModal: (currentEvent: EventInfo, roomName: String) -> Unit,
    private val openBookingDialog: (event: EventInfo, room: String) -> Unit,
) : ComponentContext by componentContext, KoinComponent, ComponentLoggable {

    override val loggableCoroutineScope = componentCoroutineScope()


    private val coroutineScope = componentCoroutineScope()

    // Use cases
    private val checkSettingsUseCase: CheckSettingsUseCase by inject()
    private val roomInfoUseCase: RoomInfoUseCase by inject()
    private val getRoomIndexUseCase: GetRoomIndexUseCase by inject()
    private val getTimeToNextEventUseCase: GetTimeToNextEventUseCase by inject()
    private val updateUseCase: UpdateUseCase by inject()
    private val timerUseCase: TimerUseCase by inject()
    private val deleteBookingUseCase: DeleteBookingUseCase by inject()

    // Timers
    private val currentTimeTimer = BootstrapperTimer(timerUseCase, coroutineScope)

    // State management
    private val mutableState = MutableStateFlow(State.defaultState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val mutableLabel = MutableSharedFlow<Label>()
    val label: SharedFlow<Label> = mutableLabel.asSharedFlow()

    // Child components
    val slotComponent = SlotComponent(
        componentContext = componentContext,
        roomName = ::getCurrentRoomName,
        openBookingDialog = openBookingDialog
    )

    init {
        initializeComponent()
    }

    /**
     * Initializes the component, checking settings and setting up timers and event listeners.
     */
    private fun initializeComponent() {
        // Load initial room data
        loadRooms()

        // Set up event listeners
        setupEventListeners()
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

        // Update time to next event periodically
        timerUseCase.timer(coroutineScope, 1.seconds) { _ ->
            withContext(Dispatchers.Main) {
                updateTimeToNextEvent()
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

        // reset select date
        currentTimeTimer.start(1.minutes) {
            withContext(Dispatchers.Main) {
                mutableState.update { it.copy(selectedDate = currentLocalDateTime) }
                slotComponent.sendIntent(SlotIntent.UpdateDate(currentLocalDateTime))
            }
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
    override fun <I> sendIntent(intent: I) {
        if (intent !is Intent) return
        logOperation("sendIntent", params = intent.toString()) {
            when (intent) {
                is Intent.OnFastBooking -> handleFastBookingIntent(intent)
                Intent.OnOpenFreeRoomModal -> handleFreeRoomIntent()
                is Intent.OnSelectRoom -> selectRoom(intent.index)
                is Intent.OnUpdateSelectDate -> updateSelectedDate(intent)
                Intent.RebootRequest -> reboot(refresh = true)
            }
        }
    }

    /**
     * Handles the fast booking intent.
     */
    private fun handleFastBookingIntent(intent: Intent.OnFastBooking) {
        val currentState = state.value
        onFastBooking(
            intent.minDuration,
            currentState.roomList[currentState.indexSelectRoom],
            currentState.roomList
        )
    }

    /**
     * Handles the free room intent.
     */
    private fun handleFreeRoomIntent() {
        val currentState = state.value
        val currentEvent = currentState.roomList[currentState.indexSelectRoom].currentEvent

        if (currentEvent != null) {
            onOpenFreeRoomModal(currentEvent, getCurrentRoomName())
        }
    }

    /**
     * Handles deleting an event.
     */
    fun handleDeleteEvent(slot: Slot) {
        slotComponent.sendIntent(
            SlotIntent.Delete(
                slot = slot,
                onDelete = {
                    deleteEventFromSlot(slot)
                }
            )
        )
    }

    /**
     * Deletes an event from a slot.
     */
    private fun deleteEventFromSlot(slot: Slot) {
        coroutineScope.launch {
            (slot as? Slot.EventSlot)?.eventInfo?.let { eventInfo ->
                deleteBookingUseCase(
                    eventInfo = eventInfo,
                    roomName = getCurrentRoomName()
                )
            }
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
            slotComponent.sendIntent(SlotIntent.UpdateDate(newDate))
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
            updateComponents(selectedRoom, state.value.selectedDate)
        }
    }

    /**
     * Updates child components with new room and date information.
     */
    private fun updateComponents(roomInfo: RoomInfo, date: LocalDateTime) {
        slotComponent.sendIntent(SlotIntent.UpdateRequest(room = roomInfo.name))
        slotComponent.sendIntent(SlotIntent.UpdateDate(date))
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

            else -> RoomsResult(
                isSuccess = false,
                roomList = listOf(RoomInfo.defaultValue),
                indexSelectRoom = 0
            )
        }
    }

    /**
     * Updates the state with room information.
     */
    private fun updateStateWithRoomsResult(roomsResult: RoomsResult) {
        mutableState.update {
            it.copy(
                isLoad = false,
                isData = roomsResult.isSuccess,
                isError = !roomsResult.isSuccess,
                roomList = roomsResult.roomList,
                indexSelectRoom = roomsResult.indexSelectRoom,
                timeToNextEvent = getTimeToNextEventUseCase(
                    state.value.roomList,
                    state.value.indexSelectRoom
                ),
            )
        }
        val selectedRoom = roomsResult.roomList[roomsResult.indexSelectRoom]
        updateComponents(selectedRoom, state.value.selectedDate)
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
            updateComponents(roomInfo, currentState.selectedDate)
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
