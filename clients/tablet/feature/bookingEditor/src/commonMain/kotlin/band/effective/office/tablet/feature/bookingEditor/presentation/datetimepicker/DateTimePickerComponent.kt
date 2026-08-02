package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.shared.core.utils.asInstant
import band.effective.office.shared.core.utils.asLocalDateTime
import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month

/**
 * Presenter for the date/time picker. Owned by [band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorViewModel];
 * its lifecycle is bound to the parent's [scope] (i.e. `viewModelScope`).
 *
 * Created via [DateTimePickerComponentFactory] so the parent ViewModel supplies only the runtime
 * params and never resolves this presenter's use case itself.
 */
class DateTimePickerComponent(
    private val scope: CoroutineScope,
    private val onSelectDate: (LocalDateTime) -> Unit,
    private val onCloseRequest: () -> Unit,
    val event: EventInfo,
    val room: String,
    val duration: Int,
    val initDate: () -> LocalDateTime,
    private val checkBookingUseCase: CheckBookingUseCase,
) {

    private val mutableState = MutableStateFlow(State.default.copy(currentDate = initDate()))
    val state: StateFlow<State> = mutableState.asStateFlow()

    fun sendIntent(intent: Intent) {
        when (intent) {
            Intent.CloseModal -> {
                onSelectDate(state.value.currentDate)
                onCloseRequest()
            }
            is Intent.OnChangeDate -> changeDate(
                intent.date.year,
                intent.date.month,
                intent.date.day,
            )
            is Intent.OnChangeTime -> changeTime(
                intent.time.hour,
                intent.time.minute
            )
        }
    }

    private fun changeDate(
        year: Int,
        month: Month,
        day: Int
    ) = scope.launch {
        val currentDate = state.value.currentDate
        val newDate = LocalDateTime(
            year = year,
            month = month,
            day = day,
            hour = currentDate.hour,
            minute = currentDate.minute,
            second = 0,
            nanosecond = 0
        )

        mutableState.update { it.copy(currentDate = newDate) }
        checkEnableDateButton(newDate, newDate.plus(duration.minutes))
    }

    private fun changeTime(
        hour: Int,
        minute: Int
    ) = scope.launch {
        val currentDate = state.value.currentDate
        val newDate = LocalDateTime(
            year = currentDate.year,
            month = currentDate.month,
            day = currentDate.day,
            hour = hour,
            minute = minute,
            second = 0,
            nanosecond = 0
        )

        mutableState.update { it.copy(currentDate = newDate) }
        checkEnableDateButton(newDate, newDate.plus(duration.minutes))
    }

    private suspend fun checkEnableDateButton(
        startDate: LocalDateTime,
        finishDate: LocalDateTime
    ) {
        // busyEvents already keeps only the events that overlap the candidate slot, so all that is
        // left is to drop the booking being edited — it is allowed to overlap itself. Match it by
        // id, the way BookingEditorViewModel.checkForBusyEvents does: comparing start times instead
        // both hid someone else's booking that happened to start at the same minute and blocked
        // moving your own booking onto free time that merely touched its old slot. A blank id means
        // a booking that does not exist yet, so there is nothing to exclude.
        val busyEvents: List<EventInfo> = checkBookingUseCase.busyEvents(
            event = event.copy(startTime = startDate, finishTime = finishDate),
            room = room
        ).filter { busy -> event.id.isBlank() || busy.id != event.id }

        val isEnabled = busyEvents.isEmpty()
        mutableState.update { it.copy(isEnabledButton = isEnabled) }
    }

    data class State(
        val currentDate: LocalDateTime,
        val isEnabledButton: Boolean
    ) {
        companion object {
            val default = State(
                currentDate = currentLocalDateTime,
                isEnabledButton = true
            )
        }
    }

    sealed interface Intent {
        object CloseModal : Intent
        data class OnChangeDate(val date: LocalDate) : Intent
        data class OnChangeTime(val time: LocalTime) : Intent
    }

    private fun LocalDateTime.plus(duration: Duration): LocalDateTime {
        val instant = this.asInstant
        val newInstant = instant.plus(duration)
        return newInstant.asLocalDateTime
    }
}

/**
 * Assisted-injection factory for [DateTimePickerComponent]: Koin supplies the use case, the caller
 * supplies the runtime params. Provided by `bookingEditorModule`.
 */
fun interface DateTimePickerComponentFactory {
    fun create(
        scope: CoroutineScope,
        onSelectDate: (LocalDateTime) -> Unit,
        onCloseRequest: () -> Unit,
        event: EventInfo,
        room: String,
        duration: Int,
        initDate: () -> LocalDateTime,
    ): DateTimePickerComponent
}
