package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.common.CrossButtonView
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

/**
 * Date/time picker UI. Rendered directly inside the `dialog<DateTimePickerRoute>` destination — that
 * navigation dialog IS the Compose Dialog window, so there is no extra Dialog here. On iOS the dialog
 * window's present animation masks the frame where calf's native UIKit picker hasn't applied our
 * colors yet, and the pickers set all colors from the theme, so no delay/alpha/warm-up masking is
 * needed. Plain `Box` (no `BoxWithConstraints`/`SubcomposeLayout`) — landscape layout only.
 */
@Composable
fun DateTimePicker(
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
