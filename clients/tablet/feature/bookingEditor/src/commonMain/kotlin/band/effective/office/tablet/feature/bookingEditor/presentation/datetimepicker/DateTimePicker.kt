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
import band.effective.office.tablet.core.ui.platform.ForcedLandscape
import band.effective.office.tablet.core.ui.platform.ScaledUiDensity
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
        onCloseRequest = {
            dateTimePickerComponent.sendIntent(DateTimePickerComponent.Intent.CloseModal)
        },
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
 * Date/time picker UI in its own Compose `Dialog`, and it has to be the ONLY dialog window in the
 * chain — the booking editor that opens it is a state-driven overlay, not a `dialog<>` destination.
 * Both halves matter on iOS: calf's pickers are native UIKit views, which receive no touches at all
 * when they sit inside a nested dialog window (Calf issue #115), while the Dialog's own present
 * animation is what masks the frame where calf hasn't applied our colors yet. The pickers set all
 * colors from the theme, so no delay/alpha/warm-up masking is needed on top. Plain `Box` (no
 * `BoxWithConstraints`/`SubcomposeLayout`) — landscape layout only.
 */
@Composable
fun DateTimePicker(
    currentDate: LocalDateTime,
    onCloseRequest: () -> Unit,
    onChangeDate: (LocalDate) -> Unit,
    onChangeTime: (LocalTime) -> Unit,
    enableDateButton: Boolean,
) {
    Dialog(
        onDismissRequest = onCloseRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        // A dialog is a window of its own on Android and a scene of its own in the Aurora fork, so
        // the rotation, the UI scale and the inactivity tracker installed by AppRoot do not reach
        // in here — re-apply them, the same way EventOrganizerView does for its popup layer.
        InactivityTracker(modifier = Modifier.fillMaxSize()) {
            ForcedLandscape {
                // Dismiss-on-tap-outside by hand, the way ModalHost does it: those wrappers make the
                // dialog's content fill the window, so there is no area left for the platform's own
                // dismissOnClickOutside to detect. Transparent, not dimmed — the modal host under
                // this window already draws the dim. The inner Box absorbs taps on the card so they
                // do not reach the dismissing one.
                ScaledUiDensity(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onCloseRequest,
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
                            DateTimePickerBody(
                                currentDate = currentDate,
                                onCloseRequest = onCloseRequest,
                                onChangeDate = onChangeDate,
                                onChangeTime = onChangeTime,
                                enableDateButton = enableDateButton,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateTimePickerBody(
    currentDate: LocalDateTime,
    onCloseRequest: () -> Unit,
    onChangeDate: (LocalDate) -> Unit,
    onChangeTime: (LocalTime) -> Unit,
    enableDateButton: Boolean,
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
                onDismissRequest = onCloseRequest,
                modifier = Modifier.fillMaxWidth(1f)
            )
            Row(
                modifier = Modifier.padding(10.dp).fillMaxHeight(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimePickerView(
                    modifier = Modifier.fillMaxWidth(0.3f),
                    currentDate = currentDate,
                    onSnap = onChangeTime
                )
                Spacer(Modifier.width(40.dp))
                DatePickerView(
                    modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight(),
                    currentDate = currentDate,
                    onChangeDate = onChangeDate,
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.Center)
                        .fillMaxWidth(0.3f),
                    onClick = { onCloseRequest() },
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
