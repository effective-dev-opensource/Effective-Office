package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerView(
    modifier: Modifier,
    currentDate: LocalDateTime,
    onSnap: (LocalTime) -> Unit,
) {
    val is24Hour = DateDisplayMapper.is24HourFormat()

    val state = rememberAdaptiveTimePickerState(
        initialHour = currentDate.hour,
        initialMinute = currentDate.minute,
        is24Hour = is24Hour
    )
    LaunchedEffect(state.hour, state.minute) {
        val hour = state.hour
        val minute = state.minute
        onSnap(LocalTime(hour, minute, 0))
    }

    AdaptiveTimePicker(
        state = state,
        modifier = modifier,
        layoutType = TimePickerLayoutType.Vertical,
        colors = TimePickerDefaults.colors(
            containerColor = LocalCustomColorsPalette.current.elevationBackground,
        )
    )
}
