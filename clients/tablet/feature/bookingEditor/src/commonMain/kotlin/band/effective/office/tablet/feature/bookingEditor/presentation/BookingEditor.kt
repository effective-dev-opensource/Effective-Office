package band.effective.office.tablet.feature.bookingEditor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.ui.button.SuccessButton
import band.effective.office.tablet.core.ui.common.AlertButton
import band.effective.office.tablet.core.ui.common.CrossButtonView
import band.effective.office.tablet.core.ui.common.EventDurationView
import band.effective.office.tablet.core.ui.common.EventOrganizerView
import band.effective.office.tablet.core.ui.common.Loader
import band.effective.office.tablet.core.ui.date.DateTimeView
import band.effective.office.tablet.core.ui.theme.h3
import band.effective.office.tablet.core.ui.theme.h6
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import band.effective.office.tablet.feature.bookingEditor.Res
import band.effective.office.tablet.feature.bookingEditor.booking_time_button
import band.effective.office.tablet.feature.bookingEditor.booking_view_title
import band.effective.office.tablet.feature.bookingEditor.create_view_title
import band.effective.office.tablet.feature.bookingEditor.delete_button
import band.effective.office.tablet.feature.bookingEditor.error
import band.effective.office.tablet.feature.bookingEditor.error_creating_event
import band.effective.office.tablet.feature.bookingEditor.error_deleting_event
import band.effective.office.tablet.feature.bookingEditor.is_time_in_past_error
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePicker
import band.effective.office.tablet.feature.bookingEditor.update_button
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource

/**
 * Keeps the editor a centered card: its fields are `fillMaxWidth` and would otherwise stretch to
 * the full width of the overlay.
 */
private val MAX_EDITOR_WIDTH = 720.dp

/**
 * Booking create/edit modal, hosted as a state-driven overlay rather than a dialog window. The
 * date/time picker is editor state, not a destination, because it needs the only `Dialog` in the
 * chain — see [DateTimePicker].
 */
@Composable
fun BookingEditor(
    viewModel: BookingEditorViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.closeEvents.collect { onClose() }
    }

    if (state.showSelectDate) {
        DateTimePicker(dateTimePickerComponent = viewModel.dateTimePickerComponent)
    }

    Box(modifier = Modifier.widthIn(max = MAX_EDITOR_WIDTH)) {
        BookingEditor(
            onDismissRequest = { viewModel.sendIntent(Intent.OnClose) },
            incrementData = { viewModel.sendIntent(Intent.OnUpdateDate(1)) },
            decrementData = { viewModel.sendIntent(Intent.OnUpdateDate(-1)) },
            onOpenDateTimePickerModal = { viewModel.sendIntent(Intent.OnOpenSelectDateDialog) },
            incrementDuration = { viewModel.sendIntent(Intent.OnUpdateLength(30)) },
            decrementDuration = { viewModel.sendIntent(Intent.OnUpdateLength(-15)) },
            onExpandedChange = { viewModel.sendIntent(Intent.OnExpandedChange) },
            onSelectOrganizer = { viewModel.sendIntent(Intent.OnSelectOrganizer(it)) },
            selectData = state.date,
            selectDuration = state.duration,
            selectOrganizer = state.selectOrganizer,
            organizers = state.selectOrganizers,
            expended = state.expanded,
            onUpdateEvent = { viewModel.sendIntent(Intent.OnUpdateEvent(viewModel.roomName)) },
            onDeleteEvent = { viewModel.sendIntent(Intent.OnDeleteEvent) },
            inputText = state.inputText,
            onInput = { viewModel.sendIntent(Intent.OnInput(it)) },
            isInputError = state.isInputError,
            onDoneInput = { viewModel.sendIntent(Intent.OnDoneInput) },
            isDeleteError = state.isErrorDelete,
            isDeleteLoad = state.isLoadDelete,
            isUpdateError = state.isErrorUpdate,
            isUpdateLoad = state.isLoadUpdate,
            isCreateError = state.isErrorCreate,
            isCreateLoad = state.isLoadCreate,
            enableUpdateButton = state.enableUpdateButton,
            isNewEvent = !state.isCreatedEvent(),
            onCreateEvent = { viewModel.sendIntent(Intent.OnBooking) },
            start = DateDisplayMapper.formatTime(state.event.startTime),
            finish = DateDisplayMapper.formatTime(state.event.finishTime),
            room = viewModel.roomName,
            isTimeInPastError = state.isTimeInPastError,
            isEditable = state.event.isEditable,
            canIncrementDuration = state.canIncrementDuration
        )
    }
}


@Composable
private fun BookingEditor(
    onDismissRequest: () -> Unit,
    incrementData: () -> Unit,
    decrementData: () -> Unit,
    onOpenDateTimePickerModal: () -> Unit,
    incrementDuration: () -> Unit,
    decrementDuration: () -> Unit,
    onExpandedChange: () -> Unit,
    onSelectOrganizer: (Organizer) -> Unit,
    selectData: LocalDateTime,
    selectDuration: Int,
    selectOrganizer: Organizer,
    organizers: List<Organizer>,
    expended: Boolean,
    onCreateEvent: () -> Unit,
    onUpdateEvent: () -> Unit,
    onDeleteEvent: () -> Unit,
    inputText: String,
    onInput: (String) -> Unit,
    isInputError: Boolean,
    onDoneInput: (String) -> Unit,
    isDeleteError: Boolean,
    isDeleteLoad: Boolean,
    isUpdateError: Boolean = false,
    isUpdateLoad: Boolean = false,
    isCreateError: Boolean = false,
    isCreateLoad: Boolean = false,
    enableUpdateButton: Boolean,
    isNewEvent: Boolean,
    start: String,
    finish: String,
    room: String,
    isTimeInPastError: Boolean,
    isEditable: Boolean = true,
    canIncrementDuration: Boolean
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val timeInPastErrorMessage = stringResource(Res.string.is_time_in_past_error)

    LaunchedEffect(isTimeInPastError) {
        if (isTimeInPastError) {
            snackbarHostState.showSnackbar(
                message = timeInPastErrorMessage,
                duration = SnackbarDuration.Short
            )
        }
    }
    Box {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(3))
                .background(MaterialTheme.colorScheme.background)
                .padding(35.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isNewEvent) stringResource(Res.string.create_view_title, room) else
                    stringResource(Res.string.booking_view_title),
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(15.dp))
            DateTimeView(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                selectDate = selectData,
                increment = incrementData,
                decrement = decrementData,
                onOpenDateTimePickerModal = onOpenDateTimePickerModal,
                showTitle = true,
                currentDate = selectData,
            )
            Spacer(modifier = Modifier.height(15.dp))
            EventDurationView(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                currentDuration = selectDuration,
                increment = incrementDuration,
                decrement = decrementDuration,
                canIncrementDuration = canIncrementDuration
            )
            Spacer(modifier = Modifier.height(15.dp))
            EventOrganizerView(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                selectOrganizers = organizers.map { it.fullName },
                expanded = expended,
                onExpandedChange = onExpandedChange,
                onSelectItem = { org -> 
                    organizers.find { it.fullName == org }?.let { organizer ->
                        onSelectOrganizer(organizer)
                    } ?: run {
                        // If organizer not found, use the first one or default
                        val fallbackOrganizer = organizers.firstOrNull() ?: Organizer.default
                        onSelectOrganizer(fallbackOrganizer)
                    }
                },
                onInput = onInput,
                isInputError = isInputError,
                onDoneInput = onDoneInput,
                inputText = inputText
            )
            Spacer(modifier = Modifier.height(25.dp))
            if (isNewEvent) {
                SuccessButton(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    onClick = onCreateEvent,
                    enable = enableUpdateButton && !isCreateLoad
                ) {
                    when {
                        isCreateLoad -> Loader()
                        isCreateError -> Text(
                            text = stringResource(Res.string.error_creating_event),
                            style = MaterialTheme.typography.h6
                        )
                        else -> Text(
                            text = stringResource(Res.string.booking_time_button, start, finish),
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            } else {
                SuccessButton(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    onClick = onUpdateEvent,
                    enable = enableUpdateButton && !isUpdateLoad && isEditable
                ) {
                    when {
                        isUpdateLoad -> Loader()
                        isUpdateError -> Text(
                            text = stringResource(Res.string.error),
                            style = MaterialTheme.typography.h6
                        )
                        else -> Text(
                            text = stringResource(Res.string.update_button),
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                AlertButton(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    onClick = onDeleteEvent,
                    enabled = isEditable
                ) {
                    when {
                        isDeleteLoad -> Loader()
                        isDeleteError -> Text(
                            text = stringResource(Res.string.error_deleting_event),
                            style = MaterialTheme.typography.h6
                        )

                        else -> {
                            Text(
                                text = stringResource(Res.string.delete_button),
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
            }
        }
        CrossButtonView(
            Modifier
                .fillMaxWidth().padding(35.dp),
            onDismissRequest = onDismissRequest
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) { snackbarData ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Text(
                    text = snackbarData.visuals.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
