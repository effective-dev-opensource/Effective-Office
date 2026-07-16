package band.effective.office.tablet.feature.bookingEditor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePickerComponent
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePickerComponentFactory
import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.EventInfoMapper
import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.UpdateEventComponentStateToEventInfoMapper
import io.github.aakira.napier.Napier
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel responsible for editing booking events.
 * Handles creating new bookings and updating existing ones.
 */
const val DURATION_INCREMENT_MINUTES = 30
const val DELETE_SUCCESS_DELAY = 2000L

class BookingEditorViewModel(
    private val organizersInfoUseCase: OrganizersInfoUseCase,
    private val checkBookingUseCase: CheckBookingUseCase,
    private val updateBookingUseCase: UpdateBookingUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val deleteBookingUseCase: DeleteBookingUseCase,
    private val eventInfoMapper: EventInfoMapper,
    private val stateToEventInfoMapper: UpdateEventComponentStateToEventInfoMapper,
    private val dateTimePickerComponentFactory: DateTimePickerComponentFactory,
    initialEvent: EventInfo,
    val roomName: String,
) : ViewModel() {

    private val coroutineScope = viewModelScope

    val dateTimePickerComponent: DateTimePickerComponent by lazy {
        dateTimePickerComponentFactory.create(
            scope = coroutineScope,
            onSelectDate = { newDate -> updateEventDate(newDate) },
            onCloseRequest = { mutableState.update { it.copy(showSelectDate = false) } },
            event = initialEvent,
            room = roomName,
            duration = state.value.duration,
            initDate = { state.value.date }
        )
    }

    // State management
    private val mutableState = MutableStateFlow(eventInfoMapper.mapToUpdateBookingState(initialEvent))
    val state = mutableState.asStateFlow()

    private val closeChannel = Channel<Unit>(Channel.BUFFERED)
    val closeEvents = closeChannel.receiveAsFlow()

    init {
        loadOrganizers()
    }

    private fun requestClose() {
        coroutineScope.launch { closeChannel.send(Unit) }
    }

    /**
     * Handles intents from the UI
     */
    fun sendIntent(intent: Intent) {
        when (intent) {
            Intent.OnBooking -> createNewEvent()
            Intent.OnClose -> requestClose()
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
                requestClose()
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
                requestClose()
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
        updateButtonState(
            inputError = isOrganizerIncorrect,
            busyEvent = isBusyEvent
        )
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
     * Updates the button state based on validation
     */
    private fun updateButtonState(
        inputError: Boolean,
        busyEvent: Boolean
    ) = mutableState.update {
        it.copy(enableUpdateButton = !inputError && !busyEvent && !it.isTimeInPastError)
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
                canIncrementDuration = canIncrementDuration
            )

            if (selectOrganizer != Organizer.default) {
                updateButtonState(
                    inputError = isInputError,
                    busyEvent = busyEvents.isNotEmpty() || isFinishTimeExceeded
                )
            }
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
                canIncrementDuration = canIncrementDuration
            )

            updateButtonState(
                inputError = !organizers.contains(resolvedOrganizer),
                busyEvent = busyEvents.isNotEmpty() || isFinishTimeExceeded
            )
        }
    }

    /**
     * Gets the current time
     */
    private fun getCurrentTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

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
                canIncrementDuration = canIncrementDuration
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
                requestClose()
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
            )
        }
        updateButtonState(
            inputError = false,
            busyEvent = state.value.isBusyEvent,
        )
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
}
