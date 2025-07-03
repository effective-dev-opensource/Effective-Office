package band.effective.office.tablet.feature.main.presentation.datetimepicker.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerView(
    modifier: Modifier = Modifier,
    currentDate: LocalDateTime,
    onSnap: (LocalTime) -> Unit
) {
    val state = rememberAdaptiveTimePickerState(
        initialHour = currentDate.hour,
        initialMinute = currentDate.minute,
        is24Hour = true
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
    )
}