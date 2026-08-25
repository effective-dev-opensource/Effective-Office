package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import band.effective.office.tablet.core.ui.common.CrossButtonView
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker
import band.effective.office.tablet.core.ui.inactivity.LocalInactivityTracking
import band.effective.office.tablet.core.ui.platform.DialogSceneFrame
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.header8
import band.effective.office.tablet.core.ui.time_booked
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components.DatePickerView
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components.TimePickerView
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

/** Opens the picker for [dateTimePickerComponent], translating its state and intents. */
@Composable
fun DateTimePicker(dateTimePickerComponent: DateTimePickerComponent) {
    val state by dateTimePickerComponent.state.collectAsState()

    DateTimePicker(
        currentDate = state.currentDate,
        onConfirmRequest = { dateTimePickerComponent.sendIntent(DateTimePickerComponent.Intent.CloseModal) },
        onCancelRequest = { dateTimePickerComponent.sendIntent(DateTimePickerComponent.Intent.CancelAndClose) },
        onChangeDate = {
            dateTimePickerComponent.sendIntent(DateTimePickerComponent.Intent.OnChangeDate(it))
        },
        onChangeTime = {
            dateTimePickerComponent.sendIntent(DateTimePickerComponent.Intent.OnChangeTime(it))
        },
        enableDateButton = state.isEnabledButton,
    )
}

/**
 * The picker owns the only `Dialog` in the chain, and re-applies to its own scene what the root
 * installed and the scene does not inherit: the inactivity tracker and the Aurora window frame.
 * See Navigation in clients/tablet/composeApp/README.md.
 */
@Composable
fun DateTimePicker(
    currentDate: LocalDateTime,
    onConfirmRequest: () -> Unit,
    onCancelRequest: () -> Unit,
    onChangeDate: (LocalDate) -> Unit,
    onChangeTime: (LocalTime) -> Unit,
    enableDateButton: Boolean,
) {
    // The pickers are native views on iOS and take their touches themselves, so the tracker around
    // them never sees a date being picked. Their reported changes are the interaction it can see.
    val tracking = LocalInactivityTracking.current
    val onDatePicked: (LocalDate) -> Unit = {
        tracking.onUserInteraction()
        onChangeDate(it)
    }
    val onTimePicked: (LocalTime) -> Unit = {
        tracking.onUserInteraction()
        onChangeTime(it)
    }

    Dialog(
        onDismissRequest = onCancelRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        InactivityTracker {
            DialogSceneFrame {
                // The frame fills the window, leaving the platform no area outside the content
                // to detect a tap in, so the dismiss is drawn here the way ModalHost draws it.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCancelRequest,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .fillMaxWidth(0.8f)
                                .clip(RoundedCornerShape(3))
                                .background(LocalCustomColorsPalette.current.elevationBackground)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CrossButtonView(
                                    onDismissRequest = onCancelRequest,
                                    modifier = Modifier.fillMaxWidth(1f)
                                )
                                Row(
                                    modifier = Modifier.padding(10.dp).fillMaxHeight(0.8f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // TimePicker's own room: enough for the 12-hour AM/PM column
                                    // that AdaptiveTimePicker draws when the system is 12-hour.
                                    TimePickerView(
                                        modifier = Modifier.fillMaxWidth(0.33f),
                                        currentDate = currentDate,
                                        onSnap = onTimePicked
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    // Material3 DatePicker inside calf's AdaptiveDatePicker on
                                    // Android needs a hair more than 0.6 or the Saturday column
                                    // gets clipped.
                                    DatePickerView(
                                        modifier = Modifier.fillMaxWidth(0.65f).fillMaxHeight(),
                                        currentDate = currentDate,
                                        onChangeDate = onDatePicked,
                                    )
                                }
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Button(
                                        modifier = Modifier.align(Alignment.Center)
                                            .fillMaxWidth(0.3f),
                                        onClick = {
                                            onConfirmRequest()
                                        },
                                        enabled = enableDateButton,
                                        colors = buttonColors(
                                            containerColor = LocalCustomColorsPalette.current.pressedPrimaryButton
                                        )
                                    ) {
                                        Text(
                                            text = when (enableDateButton) {
                                                true -> DateDisplayMapper.formatForPicker(currentDate)
                                                false -> stringResource(band.effective.office.tablet.core.ui.Res.string.time_booked)
                                            },
                                            style = header8,
                                            color = LocalCustomColorsPalette.current.primaryTextAndIcon,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
