package band.effective.office.tablet.feature.bookingEditor.presentation

import band.effective.office.shared.core.domain.Either
import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.shared.core.domain.unbox
import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.tablet.core.domain.useCase.CreateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.DeleteBookingUseCase
import band.effective.office.tablet.core.domain.useCase.OrganizersInfoUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateBookingUseCase
import band.effective.office.shared.core.utils.asInstant
import band.effective.office.shared.core.utils.asLocalDateTime
import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.shared.core.utils.componentCoroutineScope
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePickerComponent
import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.EventInfoMapper
import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.UpdateEventComponentStateToEventInfoMapper
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import io.github.aakira.napier.Napier
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Component responsible for editing booking events.
 * Handles creating new bookings and updating existing ones.
 */
const val DURATION_INCREMENT_MINUTES = 30
const val DELETE_SUCCESS_DELAY = 2000L
class BookingEditorComponent(
    componentContext: ComponentContext,
    initialEvent: EventInfo,
    val roomName: String,
    private val onCloseRequest: () -> Unit,
) : ComponentContext by componentContext, KoinComponent, ModalWindow {

    val dateTimePickerComponent: DateTimePickerComponent by lazy {
        DateTimePickerComponent(
            componentContext = componentContext,
            onSelectDate = { newDate -> updateEventDate(newDate) },
            onCloseRequest = { mutableState.update { it.copy(showSelectDate = false) } },
            event = initialEvent,
            room = roomName,
            duration = state.value.duration,
            initDate = { state.value.date }
        )
    }

    private val coroutineScope = componentCoroutineScope()

    // Use cases
    private val organizersInfoUseCase: OrganizersInfoUseCase by inject()
    private val checkBookingUseCase: CheckBookingUseCase by inject()
    private val updateBookingUseCase: UpdateBookingUseCase by inject()
    private val createBookingUseCase: CreateBookingUseCase by inject()
    private val deleteBookingUseCase: DeleteBookingUseCase by inject()

    // Mappers
    private val eventInfoMapper: EventInfoMapper by inject()
    private val stateToEventInfoMapper: UpdateEventComponentStateToEventInfoMapper by inject()

    // State management
    private val mutableState = MutableStateFlow(eventInfoMapper.mapToUpdateBookingState(initialEvent))
    val state = mutableState.asStateFlow()

    // Navigation
    private val navigation = StackNavigation<ModalConfig>()

    val childStack = childStack(
        source = navigation,
        initialConfiguration = ModalConfig.UpdateModal,
        serializer = ModalConfig.serializer(),
        childFactory = { config, _ -> config },
    )

    init {
        loadOrganizers()
        // Prime the validation from the event we were opened with: the checks otherwise only ever
        // run in response to an edit.
        updateEventDetails()
    }

    /**
     * Handles intents from the UI
     */
    fun sendIntent(intent: Intent) {
        when (intent) {
            Intent.OnBooking -> createNewEvent()
            Intent.OnClose -> onCloseRequest()
            Intent.OnCloseSelectDateDialog -> closeSelectDateDialog()
            Intent.OnDeleteEvent -> deleteEvent()
            Intent.OnDoneInput -> finalizeOrganizerSelection()
            Intent.OnExpandedChange -> toggleExpandedState()
            is Intent.OnInput -> handleOrganizerInput(intent.input)
            Intent.OnOpenSelectDateDialog -> openSelectDateDialog()
            is Intent.OnSelectOrganizer -> selectOrganizer(intent.newOrganizer)
            is Intent.OnSetDate -> updateEventDate(intent.calendar)
            is Intent.OnUpdateDate -> updateEventDetails(daysToAdd = intent.updateInDays)
            is Intent.OnUpdateEvent -> updateExistingEvent()
            is Intent.OnUpdateLength -> updateEventDetails(durationChange = intent.update)
        }
    }

    /**
     * Updates an existing event in the database
     */
    private fun updateExistingEvent() = coroutineScope.launch {
        mutableState.update { it.copy(isLoadUpdate = true) }
        val updateBookingResult = withContext(Dispatchers.IO) {
            updateBookingUseCase(
                roomName = roomName,
                eventInfo = stateToEventInfoMapper.map(state.value)
            )
        }
        updateBookingResult.unbox(
            errorHandler = {
                Napier.d { "Update booking failed: ${it.description}" }
                mutableState.update {
                    it.copy(
                        isLoadUpdate = false,
                        isErrorUpdate = true
                    )
                }
            },
            successHandler = {
                mutableState.update { it.copy(isLoadUpdate = false) }
                onCloseRequest()
            }
        )
    }

    /**
     * Loads the list of organizers from the database
     */
    private fun loadOrganizers() = coroutineScope.launch {
        val organizers = organizersInfoUseCase().unbox(errorHandler = { emptyList() })
        mutableState.update {
            it.copy(
                organizers = organizers,
                selectOrganizers = organizers,
            )
        }
    }

    /**
     * Deletes the current event
     */
    private fun deleteEvent() = coroutineScope.launch {
        mutableState.update { it.copy(isLoadDelete = true) }
        val deleteResult = withContext(Dispatchers.IO) {
            deleteBookingUseCase(roomName, state.value.event)
        }

        when (deleteResult) {
            is Either.Error -> {
                mutableState.update {
                    it.copy(
                        isLoadDelete = false,
                        isErrorDelete = true,
                    )
                }
            }

            is Either.Success -> {
                delay(DELETE_SUCCESS_DELAY)
                onCloseRequest()
            }
        }
    }

    /**
     * Finalizes the organizer selection based on the input text
     */
    private fun finalizeOrganizerSelection() = with(state.value) {
        val input = inputText.lowercase()
        val organizer = findOrganizerByName(input) ?: event.organizer
        val isOrganizerIncorrect = !organizers.contains(organizer)

        mutableState.update {
            it.copy(
                selectOrganizer = organizer,
                inputText = organizer.fullName,
                isInputError = isOrganizerIncorrect,
            )
        }
    }

    /**
     * Finds an organizer by name (partial match)
     */
    private fun findOrganizerByName(name: String): Organizer? {
        return state.value.selectOrganizers.firstOrNull {
            it.fullName.lowercase().contains(name.lowercase())
        }
    }

    /**
     * Handles input in the organizer field
     */
    private fun handleOrganizerInput(input: String) {
        val filteredOrganizers = state.value.organizers
            .filter { it.fullName.lowercase().contains(input.lowercase()) }
            .sortedBy { it.fullName.lowercase().indexOf(input.lowercase()) }

        mutableState.update {
            it.copy(
                inputText = input,
                selectOrganizers = filteredOrganizers
            )
        }
    }

    /**
     * Updates the event date
     */
    private fun updateEventDate(newDate: LocalDateTime) = coroutineScope.launch {
        with(state.value) {
            val busyEvents = checkForBusyEvents(
                date = newDate,
                duration = duration,
                organizer = selectOrganizer
            )
            val isTimeInPast = newDate <= getCurrentTime()

            val newFinishTime = newDate.asInstant.plus(duration.minutes).asLocalDateTime
            val finishWorkTime = OfficeTime.finishWorkTime(newDate.date)
            val isFinishTimeExceeded = newFinishTime > finishWorkTime

            val nextIncrementFinishTime = newDate.asInstant.plus((duration + DURATION_INCREMENT_MINUTES).minutes).asLocalDateTime
            val canIncrementDuration = nextIncrementFinishTime <= finishWorkTime

            updateStateWithNewEventDetails(
                newDate = newDate,
                newDuration = duration,
                newOrganizer = selectOrganizer,
                busyEvents = busyEvents,
                isTimeInPast = isTimeInPast,
                isFinishTimeExceeded = isFinishTimeExceeded,
                canIncrementDuration = canIncrementDuration
            )
        }
    }

    /**
     * Updates event details (date, duration, organizer)
     */
    private fun updateEventDetails(
        daysToAdd: Int = 0,
        durationChange: Int = 0,
        newOrganizer: Organizer = state.value.selectOrganizer
    ) = coroutineScope.launch {
        with(state.value) {
            val newDate = date.asInstant.plus(daysToAdd.days).asLocalDateTime
            val newDuration = duration + durationChange
            val resolvedOrganizer = organizers.firstOrNull {
                it.fullName == newOrganizer.fullName
            } ?: event.organizer
            val isTimeInPast = newDate <= getCurrentTime()

            val finishWorkTime = OfficeTime.finishWorkTime(newDate.date)
            val newFinishTime = newDate.asInstant.plus(newDuration.minutes).asLocalDateTime
            val isFinishTimeExceeded = newFinishTime > finishWorkTime

            val nextIncrementFinishTime = newDate.asInstant.plus((newDuration + DURATION_INCREMENT_MINUTES).minutes).asLocalDateTime
            val canIncrementDuration = nextIncrementFinishTime <= finishWorkTime

            val busyEvents = checkForBusyEvents(
                date = newDate,
                duration = newDuration,
                organizer = resolvedOrganizer
            )

            updateStateWithNewEventDetails(
                newDate = newDate,
                newDuration = newDuration,
                newOrganizer = resolvedOrganizer,
                busyEvents = busyEvents,
                isTimeInPast = isTimeInPast,
                isFinishTimeExceeded = isFinishTimeExceeded,
                canIncrementDuration = canIncrementDuration
            )
        }
    }

    /**
     * Gets the current time
     */
    private fun getCurrentTime(): LocalDateTime = currentLocalDateTime

    /**
     * Checks for busy events that conflict with the given parameters
     */
    private suspend fun checkForBusyEvents(
        date: LocalDateTime,
        duration: Int,
        organizer: Organizer
    ): List<EventInfo> {
        val eventToCheck = createEventInfo(
            id = state.value.event.id,
            startTime = date,
            duration = duration,
            organizer = organizer
        )

        val currentEventId = state.value.event.id
        return checkBookingUseCase.busyEvents(
            event = eventToCheck,
            room = roomName
        ).filter { busyEvent ->
            // Exclude the current event being edited (if it's an update)
            if (busyEvent.id == currentEventId && !currentEventId.isBlank()) {
                return@filter false
            }

            // Check for any overlap between events
            val newEventStart = eventToCheck.startTime.asInstant
            val newEventEnd = eventToCheck.finishTime.asInstant
            val busyEventStart = busyEvent.startTime.asInstant
            val busyEventEnd = busyEvent.finishTime.asInstant

            // Events overlap if one starts before the other ends
            (newEventStart < busyEventEnd && newEventEnd > busyEventStart)
        }
    }

    /**
     * Updates the state with new event details
     */
    private fun updateStateWithNewEventDetails(
        newDate: LocalDateTime,
        newDuration: Int,
        newOrganizer: Organizer,
        busyEvents: List<EventInfo>,
        isTimeInPast: Boolean,
        isFinishTimeExceeded: Boolean,
        canIncrementDuration: Boolean
    ) {
        val updatedEvent = createEventInfo(
            id = state.value.event.id,
            startTime = newDate,
            duration = newDuration,
            organizer = newOrganizer
        )

        mutableState.update {
            it.copy(
                date = newDate,
                duration = newDuration,
                selectOrganizer = newOrganizer,
                event = updatedEvent,
                isBusyEvent = busyEvents.isNotEmpty(),
                isTimeInPastError = isTimeInPast,
                isFinishTimeExceeded = isFinishTimeExceeded,
                canIncrementDuration = canIncrementDuration,
                // Any edit invalidates a previous failure, which nothing else clears.
                isErrorUpdate = false,
                isErrorCreate = false,
                isErrorDelete = false,
            )
        }
    }

    /**
     * Creates a new event in the database
     */
    private fun createNewEvent() = coroutineScope.launch {
        mutableState.update { it.copy(isLoadCreate = true) }
        val eventToCreate = stateToEventInfoMapper.map(state.value)
        val createBookingResult = withContext(Dispatchers.IO) {
            createBookingUseCase(roomName = roomName, eventInfo = eventToCreate)
        }
        createBookingResult.unbox(
            errorHandler = {
                mutableState.update {
                    it.copy(
                        isLoadCreate = false,
                        isErrorCreate = true,
                    )
                }
            },
            successHandler = {
                mutableState.update { it.copy(isLoadCreate = false) }
                onCloseRequest()
            }
        )
    }

    /**
     * Creates an EventInfo object with the given parameters
     */
    private fun createEventInfo(
        id: String,
        startTime: LocalDateTime,
        duration: Int,
        organizer: Organizer,
    ): EventInfo {
        return EventInfo(
            startTime = startTime,
            finishTime = startTime.asInstant.plus(duration.minutes).asLocalDateTime,
            organizer = organizer,
            id = id,
            isLoading = false,
            isEditable = state.value.event.isEditable,
        )
    }

    /**
     * Selects an organizer
     */
    private fun selectOrganizer(organizer: Organizer) {
        mutableState.update {
            it.copy(
                selectOrganizer = organizer,
                inputText = organizer.fullName,
                isInputError = false,
            )
        }
    }

    /**
     * Toggles the expanded state
     */
    private fun toggleExpandedState() = mutableState.update { it.copy(expanded = !it.expanded) }


    /**
     * Opens the select date dialog
     */
    private fun openSelectDateDialog() = mutableState.update { it.copy(showSelectDate = true) }


    /**
     * Closes the select date dialog
     */
    private fun closeSelectDateDialog() = mutableState.update { it.copy(showSelectDate = false) }

    @Serializable
    sealed interface ModalConfig {
        @Serializable
        object UpdateModal : ModalConfig

        @Serializable
        object SuccessModal : ModalConfig

        @Serializable
        object FailureModal : ModalConfig
    }
}
