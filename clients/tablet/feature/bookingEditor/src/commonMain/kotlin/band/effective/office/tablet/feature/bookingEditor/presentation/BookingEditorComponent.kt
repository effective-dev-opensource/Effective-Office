package band.effective.office.tablet.feature.bookingEditor.presentation

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.unbox
import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.tablet.core.domain.useCase.CreateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.OrganizersInfoUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateBookingUseCase
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.domain.util.defaultTimeZone
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Component responsible for editing booking events.
 * Handles creating new bookings and updating existing ones.
 */
class BookingEditorComponent(
    componentContext: ComponentContext,
    initialEvent: EventInfo,
    val roomName: String,
    private val onDeleteEvent: (Slot) -> Unit,
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
        loadOrganizers().also { Napier.d { "[BookingEditorComponent] Initialized with eventId=${initialEvent.id}, room=$roomName" } }
    }

    /**
     * Handles intents from the UI
     */
    fun sendIntent(intent: Intent) {
        when (intent) {
            Intent.OnBooking -> createNewEvent().also { Napier.d { "[BookingEditorComponent] Intent: OnBooking" } }
            Intent.OnClose -> onCloseRequest().also { Napier.d { "[BookingEditorComponent] Intent: OnClose" } }
            Intent.OnCloseSelectDateDialog -> closeSelectDateDialog().also { Napier.d { "[BookingEditorComponent] Intent: OnCloseSelectDateDialog" } }
            Intent.OnDeleteEvent -> deleteEvent().also { Napier.d { "[BookingEditorComponent] Intent: OnDeleteEvent" } }
            Intent.OnDoneInput -> finalizeOrganizerSelection().also { Napier.d { "[BookingEditorComponent] Intent: OnDoneInput" } }
            Intent.OnExpandedChange -> toggleExpandedState().also { Napier.d { "[BookingEditorComponent] Intent: OnExpandedChange" } }
            is Intent.OnInput -> handleOrganizerInput(intent.input).also { Napier.d { "[BookingEditorComponent] Intent: OnInput, input=${intent.input}" } }
            Intent.OnOpenSelectDateDialog -> openSelectDateDialog().also { Napier.d { "[BookingEditorComponent] Intent: OnOpenSelectDateDialog" } }
            is Intent.OnSelectOrganizer -> selectOrganizer(intent.newOrganizer).also { Napier.d { "[BookingEditorComponent] Intent: OnSelectOrganizer, organizer=${intent.newOrganizer.fullName}" } }
            is Intent.OnSetDate -> updateEventDate(intent.calendar).also { Napier.d { "[BookingEditorComponent] Intent: OnSetDate, date=${intent.calendar}" } }
            is Intent.OnUpdateDate -> updateEventDetails(daysToAdd = intent.updateInDays).also { Napier.d { "[BookingEditorComponent] Intent: OnUpdateDate, daysToAdd=${intent.updateInDays}" } }
            is Intent.OnUpdateEvent -> updateExistingEvent().also { Napier.d { "[BookingEditorComponent] Intent: OnUpdateEvent" } }
            is Intent.OnUpdateLength -> updateEventDetails(durationChange = intent.update).also { Napier.d { "[BookingEditorComponent] Intent: OnUpdateLength, durationChange=${intent.update}" } }
        }
    }

    /**
     * Updates an existing event in the database
     */
    private fun updateExistingEvent() = coroutineScope.launch {
        mutableState.update { it.copy(isLoadUpdate = true) }
        Napier.d { "[BookingEditorComponent] Updating event: eventId=${state.value.event.id}" }
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
                Napier.i { "[BookingEditorComponent] Update booking succeeded: eventId=${state.value.event.id}" }
                mutableState.update { it.copy(isLoadUpdate = false) }
                onCloseRequest()
            }
        )
    }

    /**
     * Loads the list of organizers from the database
     */
    private fun loadOrganizers() = coroutineScope.launch {
        Napier.d { "[BookingEditorComponent] Loading organizers" }
        val organizers = organizersInfoUseCase().unbox(errorHandler = { emptyList() })
        mutableState.update {
            it.copy(
                organizers = organizers,
                selectOrganizers = organizers,
            )
        }
        Napier.d { "[BookingEditorComponent] Loaded ${organizers.size} organizers" }
    }

    /**
     * Deletes the current event
     */
    private fun deleteEvent() = coroutineScope.launch {
        mutableState.update { it.copy(isLoadDelete = true) }
        Napier.d { "[BookingEditorComponent] Deleting event: eventId=${state.value.event.id}" }
        onDeleteEvent(eventInfoMapper.mapToSlot(state.value.event))
        mutableState.update { it.copy(isLoadDelete = false) }
        onCloseRequest()
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
        it.copy(enableUpdateButton = !inputError && !busyEvent)
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

            updateStateWithNewEventDetails(
                newDate = newDate,
                newDuration = duration,
                newOrganizer = selectOrganizer,
                busyEvents = busyEvents
            )

            if (selectOrganizer != Organizer.default) {
                updateButtonState(
                    inputError = isInputError,
                    busyEvent = busyEvents.isNotEmpty()
                )
            }
            Napier.d { "[BookingEditorComponent] Updated event date: newDate=$newDate" }
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

            val busyEvents = checkForBusyEvents(
                date = newDate,
                duration = newDuration,
                organizer = resolvedOrganizer
            )

            if (isValidEventTime(newDate, newDuration)) {
                updateStateWithNewEventDetails(
                    newDate = newDate,
                    newDuration = newDuration,
                    newOrganizer = resolvedOrganizer,
                    busyEvents = busyEvents
                )

                updateButtonState(
                    inputError = !organizers.contains(resolvedOrganizer),
                    busyEvent = busyEvents.isNotEmpty()
                )
            }
            Napier.d { "[BookingEditorComponent] Updated event details: date=$newDate, duration=$newDuration" }
        }
    }

    /**
     * Checks if the event time is valid
     */
    private fun isValidEventTime(date: LocalDateTime, duration: Int): Boolean {
        val today = getTodayStartTime()
        val officeEndTime = OfficeTime.finishWorkTime(date.date)
        val eventEndTime = date.asInstant.plus(duration.minutes).asLocalDateTime

        return duration > 0 && date > today && eventEndTime < officeEndTime
    }

    /**
     * Gets the start time of today
     */
    private fun getTodayStartTime(): LocalDateTime =
        currentLocalDateTime.date.atStartOfDayIn(defaultTimeZone).asLocalDateTime

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
        busyEvents: List<EventInfo>
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
                isBusyEvent = busyEvents.isNotEmpty()
            )
        }
    }

    /**
     * Creates a new event in the database
     */
    private fun createNewEvent() = coroutineScope.launch {
        mutableState.update { it.copy(isLoadCreate = true) }
        Napier.d { "[BookingEditorComponent] Creating new event" }
        val eventToCreate = stateToEventInfoMapper.map(state.value)
        val createBookingResult = withContext(Dispatchers.IO) {
            createBookingUseCase(roomName = roomName, eventInfo = eventToCreate)
        }
        createBookingResult.unbox(
            errorHandler = {
                Napier.e { "[BookingEditorComponent] Create booking failed: ${it.description}" }
                mutableState.update {
                    it.copy(
                        isLoadCreate = false,
                        isErrorCreate = true,
                    )
                }
            },
            successHandler = {
                Napier.i { "[BookingEditorComponent] Create booking succeeded" }
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
