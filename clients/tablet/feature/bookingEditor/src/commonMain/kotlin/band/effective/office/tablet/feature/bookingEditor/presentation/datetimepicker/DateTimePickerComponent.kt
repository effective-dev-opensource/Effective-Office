package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.core.ui.utils.componentCoroutineScope
import com.arkivanov.decompose.ComponentContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DateTimePickerComponent(
    private val componentContext: ComponentContext,
    private val onSelectDate: (LocalDateTime) -> Unit,
    private val onCloseRequest: () -> Unit,
    val event: EventInfo,
    val room: String,
    val duration: Int,
    val initDate: () -> LocalDateTime,
) : ComponentContext by componentContext, ModalWindow, KoinComponent {

    private val checkBookingUseCase: CheckBookingUseCase by inject()
    private val scope = componentCoroutineScope()

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
        dayOfMonth: Int
    ) = scope.launch {
        val currentDate = state.value.currentDate
        val newDate = LocalDateTime(
            year = year,
            month = month,
            day = dayOfMonth,
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
        val busyEvents: List<EventInfo> = checkBookingUseCase.busyEvents(
            event = event.copy(startTime = startDate, finishTime = finishDate),
            room = room
        ).filter { it.startTime != startDate }

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
