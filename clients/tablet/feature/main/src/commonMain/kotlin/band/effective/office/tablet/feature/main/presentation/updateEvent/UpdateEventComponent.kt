package band.effective.office.tablet.feature.main.presentation.updateEvent

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.unbox
import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.tablet.core.domain.useCase.OrganizersInfoUseCase
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.domain.util.defaultTimeZone
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
import band.effective.office.tablet.feature.main.domain.CreateBookingUseCase
import band.effective.office.tablet.feature.main.domain.UpdateBookingUseCase
import band.effective.office.tablet.feature.main.domain.mapper.EventInfoMapper
import band.effective.office.tablet.feature.main.domain.mapper.UpdateEventComponentStateToEventInfoMapper
import band.effective.office.tablet.feature.main.presentation.datetimepicker.DateTimePickerComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UpdateEventComponent(
    componentContext: ComponentContext,
    event: EventInfo,
    val room: String,
    private val onDelete: (Slot) -> Unit,
    private val onCloseRequest: () -> Unit,
) : ComponentContext by componentContext, KoinComponent, ModalWindow {

    val dateTimePickerComponent: DateTimePickerComponent by lazy {
        DateTimePickerComponent(
            componentContext = componentContext,
            onSelectDate = { newDate -> setDay(newDate) },
            onCloseRequest = { mutableState.update { it.copy(showSelectDate = false) } },
            event = event,
            room = room,
            duration = state.value.duration,
            initDate = { state.value.date }
        )
    }

    private val scope = componentCoroutineScope()

    val organizersInfoUseCase: OrganizersInfoUseCase by inject()
    val checkBookingUseCase: CheckBookingUseCase by inject()
    val updateBookingUseCase: UpdateBookingUseCase by inject()
    val createBookingUseCase: CreateBookingUseCase by inject()

    val eventInfoMapper: EventInfoMapper by inject()
    val stateToEventInfoMapper: UpdateEventComponentStateToEventInfoMapper by inject()

    private val mutableState = MutableStateFlow(eventInfoMapper.mapToUpdateBookingState(event))
    val state = mutableState.asStateFlow()

    private val navigation = StackNavigation<ModalConfig>()

    val childStack = childStack(
        source = navigation,
        initialConfiguration = ModalConfig.UpdateModal,
        serializer = ModalConfig.serializer(),
        childFactory = { config, _ -> config },
    )


    init {
        loadOrganizers()
    }

    fun sendIntent(intent: Intent) {
        when (intent) {
            Intent.OnBooking -> createEvent()
            Intent.OnClose -> onCloseRequest()
            Intent.OnCloseSelectDateDialog -> mutableState.update { it.copy(showSelectDate = false) }
            Intent.OnDeleteEvent -> cancel()
            Intent.OnDoneInput -> onDoneInput()
            Intent.OnExpandedChange -> mutableState.update { it.copy(expanded = !it.expanded) }
            is Intent.OnInput -> onInput(intent.input)
            Intent.OnOpenSelectDateDialog -> mutableState.update { it.copy(showSelectDate = true) }
            is Intent.OnSelectOrganizer -> mutableState.update {
                it.copy(
                    selectOrganizer = intent.newOrganizer,
                    inputText = intent.newOrganizer.fullName,
                )
            }

            is Intent.OnSetDate -> setDay(intent.calendar)
            is Intent.OnUpdateDate -> updateInfo(changeData = intent.updateInDays)
            is Intent.OnUpdateEvent -> updateEvent()
            is Intent.OnUpdateLength -> updateInfo(changeDuration = intent.update)
        }
    }

    private fun updateEvent() = scope.launch {
        updateBookingUseCase(roomName = room, eventInfo = stateToEventInfoMapper.map(state.value))
        onCloseRequest()
    }

    private fun loadOrganizers() = scope.launch {
        val organizers = organizersInfoUseCase().unbox(errorHandler = { emptyList() })
        mutableState.update {
            it.copy(
                organizers = organizers,
                selectOrganizers = organizers,
            )
        }
    }

    private fun cancel() {
        onDelete(eventInfoMapper.mapToSlot(state.value.event))
        onCloseRequest()
    }

    private fun onDoneInput() = with(state.value) {
        val input = inputText.lowercase()
        val organizer = selectOrganizers.firstOrNull { it.fullName.lowercase().contains(input) } ?: event.organizer
        val isOrganizerIncorrect = !organizers.contains(organizer)

        mutableState.update {
            it.copy(
                selectOrganizer = organizer,
                inputText = organizer.fullName,
                isInputError = isOrganizerIncorrect,
            )
        }
        checkEnableButton(
            inputError = isOrganizerIncorrect,
            busyEvent = isBusyEvent
        )
    }

    private fun onInput(input: String) {
        val newList = state.value.organizers
            .filter { it.fullName.lowercase().contains(input.lowercase()) }
            .sortedBy { it.fullName.lowercase().indexOf(input.lowercase()) }
        mutableState.update { it.copy(inputText = input, selectOrganizers = newList, expanded = true) }
    }

    private fun checkEnableButton(
        inputError: Boolean,
        busyEvent: Boolean
    ) = mutableState.update { it.copy(enableUpdateButton = !inputError && !busyEvent) }

    private fun setDay(newDate: LocalDateTime) = scope.launch {
        with(state.value) {
            val busyEvent: List<EventInfo> = checkBookingUseCase.busyEvents(
                event = copy(date = newDate).let(stateToEventInfoMapper::map),
                room = room
            ).filter { it.startTime != date }

            mutableState.update {
                it.copy(
                    date = newDate,
                    duration = duration,
                    selectOrganizer = selectOrganizer,
                    event = event(
                        id = event.id,
                        newDate = newDate,
                        newDuration = duration,
                        organizer = selectOrganizer,
                    ),
                    isBusyEvent = busyEvent.isNotEmpty()
                )
            }

            if (selectOrganizer != Organizer.default) {
                checkEnableButton(
                    inputError = isInputError,
                    busyEvent = busyEvent.isNotEmpty()
                )
            }
        }
    }

    private fun updateInfo(
        changeData: Int = 0,
        changeDuration: Int = 0,
        newOrg: Organizer = state.value.selectOrganizer
    ) = scope.launch {
        with(state.value) {
            val newDate = date.asInstant.plus(changeData.days).asLocalDateTime
            val newDuration = duration + changeDuration
            val newOrganizer = organizers.firstOrNull { it.fullName == newOrg.fullName } ?: event.organizer
            val busyEvent: List<EventInfo> = checkBookingUseCase.busyEvents(
                event = copy(
                    date = newDate,
                    duration = newDuration,
                    selectOrganizer = newOrganizer
                ).let(stateToEventInfoMapper::map),
                room = room
            ).filter { it.startTime != date }


            fun today(): LocalDateTime {
                val now = currentLocalDateTime
                return now.date.atStartOfDayIn(defaultTimeZone).asLocalDateTime
            }

            if (newDuration > 0 && newDate > today()) {
                mutableState.update {
                    it.copy(
                        date = newDate,
                        duration = newDuration,
                        selectOrganizer = newOrganizer,
                        event = event(
                            id = event.id,
                            newDate = newDate,
                            newDuration = newDuration,
                            organizer = newOrganizer,
                        ),
                        isBusyEvent = busyEvent.isNotEmpty()
                    )
                }
                checkEnableButton(
                    inputError = !organizers.contains(newOrganizer),
                    busyEvent = busyEvent.isNotEmpty()
                )
            }
        }
    }

    private fun createEvent() = scope.launch {
        val event = stateToEventInfoMapper.map(state.value)
        CoroutineScope(Dispatchers.IO).launch {
            createBookingUseCase(roomName = room, eventInfo = event)
        }
        onCloseRequest()
    }

    // TODO refactor
    private fun event(
        id: String,
        newDate: LocalDateTime,
        newDuration: Int,
        organizer: Organizer,
    ): EventInfo {
        return EventInfo(
            startTime = newDate,
            finishTime = newDate.asInstant.plus(newDuration.minutes).asLocalDateTime,
            organizer = organizer,
            id = id,
            isLoading = false,
        )
    }

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
