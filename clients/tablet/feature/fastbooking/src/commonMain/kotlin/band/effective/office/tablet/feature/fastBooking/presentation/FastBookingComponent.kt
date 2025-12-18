package band.effective.office.tablet.feature.fastBooking.presentation

import band.effective.office.shared.core.domain.Either
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.useCase.CreateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.DeleteBookingUseCase
import band.effective.office.tablet.core.domain.useCase.SelectRoomUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.shared.core.utils.asLocalDateTime
import band.effective.office.shared.core.utils.cropSeconds
import band.effective.office.shared.core.utils.currentInstant
import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.shared.core.utils.componentCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay

/**
 * Component responsible for fast booking of rooms.
 * Handles finding available rooms and creating quick bookings.
 */
class FastBookingComponent(
    private val componentContext: ComponentContext,
    val minEventDuration: Int,
    val selectedRoom: RoomInfo,
    val rooms: List<RoomInfo>,
    private val onCloseRequest: () -> Unit
) : ComponentContext by componentContext, KoinComponent, ModalWindow {

    private val coroutineScope = componentCoroutineScope()

    // Use cases
    private val selectRoomUseCase: SelectRoomUseCase by inject()
    private val createFastBookingUseCase: CreateBookingUseCase by inject()
    private val deleteBookingUseCase: DeleteBookingUseCase by inject()
    private val timerUseCase: TimerUseCase by inject()

    // Timers
    private val currentTimeTimer = BootstrapperTimer(timerUseCase, coroutineScope)

    // State management
    private val mutableState = MutableStateFlow(State.defaultState)
    val state = mutableState.asStateFlow()

    // Navigation
    private val navigation = StackNavigation<ModalConfig>()
    val childStack = childStack(
        source = navigation,
        initialConfiguration = ModalConfig.LoadingModal,
        serializer = ModalConfig.serializer(),
        childFactory = { config, _ -> config },
    )

    init {
        initializeComponent()
    }

    /**
     * Initializes the component, finding available rooms and setting up timers.
     */
    private fun initializeComponent() {
        findAvailableRoom()
        setupTimeUpdates()
    }

    /**
     * Sets up periodic time updates.
     */
    private fun setupTimeUpdates() {
        mutableState.update { it.copy(currentTime = currentLocalDateTime) }

        currentTimeTimer.start(1.minutes) {
            withContext(Dispatchers.Main) {
                mutableState.update { it.copy(currentTime = currentLocalDateTime) }
            }
        }
    }

    /**
     * Finds an available room for booking.
     */
    private fun findAvailableRoom() = coroutineScope.launch {
        try {
            val availableRoom = findRoomForBooking()

            if (availableRoom != null) {
                createEvent(availableRoom.name, minEventDuration)
            } else {
                handleNoAvailableRooms()
            }
        } catch (e: Exception) {
            Napier.e("Error finding available room", e)
            mutableState.update { it.copy(isLoad = false, isSuccess = false, isError = true) }
            navigation.push(ModalConfig.FailureModal(""))
        }
    }

    /**
     * Finds a room that can be booked for the specified duration.
     */
    private fun findRoomForBooking(): RoomInfo? {
        return selectRoomUseCase.getRoom(
            currentRoom = selectedRoom,
            rooms = rooms,
            minEventDuration = minEventDuration
        )
    }

    /**
     * Handles the case when no rooms are available for immediate booking.
     */
    private fun handleNoAvailableRooms() {
        mutableState.update { it.copy(isLoad = false, isSuccess = false) }

        val nearestFreeRoom = selectRoomUseCase.getNearestFreeRoom(rooms, minEventDuration)
        val minutesUntilAvailable = nearestFreeRoom.second.inWholeMinutes.toInt()

        mutableState.update { it.copy(minutesLeft = minutesUntilAvailable) }
        navigation.push(ModalConfig.FailureModal(nearestFreeRoom.first.name))
    }

    /**
     * Handles intents from the UI.
     */
    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.OnFreeSelectRequest -> freeRoom(intent.room)
            Intent.OnCloseWindowRequest -> onCloseRequest()
        }
    }

    /**
     * Creates a new event in the specified room.
     */
    private fun createEvent(room: String, minDuration: Int) = coroutineScope.launch {
        try {
            val eventInfo = createEventInfo(minDuration)

            when (val result = createFastBookingUseCase(room, eventInfo)) {
                is Either.Success -> {
                    delay(2000) // NOTE(radchenko): wait for the event to be created in an external service
                    handleSuccessfulEventCreation(room, eventInfo, result.data.id)
                }

                is Either.Error -> {
                    Napier.e("Failed to create event: ${result.error}")
                    handleFailedEventCreation(room)
                }
            }
        } catch (e: Exception) {
            Napier.e("Error creating event", e)
            handleFailedEventCreation(room)
        }
    }

    /**
     * Creates an EventInfo object with the given duration.
     */
    private fun createEventInfo(minDuration: Int): EventInfo {
        return EventInfo.emptyEvent.copy(
            startTime = currentLocalDateTime.cropSeconds(),
            finishTime = currentInstant.plus(minDuration.minutes).asLocalDateTime.cropSeconds()
        )
    }

    /**
     * Handles successful event creation.
     */
    private fun handleSuccessfulEventCreation(room: String, eventInfo: EventInfo, eventId: String) {
        mutableState.update {
            it.copy(
                event = eventInfo.copy(id = eventId),
                isLoad = false,
                isSuccess = true,
                isError = false
            )
        }
        navigation.push(ModalConfig.SuccessModal(room, eventInfo))
    }

    /**
     * Handles failed event creation.
     */
    private fun handleFailedEventCreation(room: String) {
        mutableState.update {
            it.copy(
                isLoad = false,
                isSuccess = false,
                isError = true
            )
        }
        navigation.push(ModalConfig.FailureModal(room))
    }

    /**
     * Frees up a room by deleting the current event.
     */
    private fun freeRoom(room: String) = coroutineScope.launch {
        try {
            mutableState.update { it.copy(isLoad = true) }

            when (val result = deleteBookingUseCase(room, state.value.event)) {
                is Either.Success -> {
                    delay(3000) // NOTE(radchenko): wait for the event to be created in an external service
                    mutableState.update { it.copy(isLoad = false) }
                    onCloseRequest()
                }

                is Either.Error -> {
                    Napier.e("Failed to free room: ${result.error}")
                    mutableState.update {
                        it.copy(
                            isLoad = false,
                            isError = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Napier.e("Error freeing room", e)
            mutableState.update {
                it.copy(
                    isLoad = false,
                    isError = true
                )
            }
        }
    }

    /**
     * Modal window configurations.
     */
    @Serializable
    sealed interface ModalConfig {
        /**
         * Shown when a booking is successfully created.
         */
        @Serializable
        data class SuccessModal(val room: String, val eventInfo: EventInfo) : ModalConfig

        /**
         * Shown when a booking cannot be created.
         */
        @Serializable
        data class FailureModal(val room: String) : ModalConfig

        /**
         * Shown while loading.
         */
        @Serializable
        object LoadingModal : ModalConfig
    }
}
