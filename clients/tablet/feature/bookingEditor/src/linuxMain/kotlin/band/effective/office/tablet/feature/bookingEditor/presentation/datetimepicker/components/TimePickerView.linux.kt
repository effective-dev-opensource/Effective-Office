package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

/**
 * Same Material3 clock face that Android's calf wraps, straight from androidx.compose.material3.
 * Follows the system time format; on ru_RU (the only supported locale on Aurora right now) that
 * comes out 24-hour, so PlatformDateFormat's AM/PM stub is never exercised.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerView(
    modifier: Modifier,
    currentDate: LocalDateTime,
    onSnap: (LocalTime) -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = currentDate.hour,
        initialMinute = currentDate.minute,
        is24Hour = DateDisplayMapper.is24HourFormat(),
    )
    LaunchedEffect(state.hour, state.minute) {
        onSnap(LocalTime(state.hour, state.minute, 0))
    }
    TimePicker(
        state = state,
        modifier = modifier,
        layoutType = TimePickerLayoutType.Vertical,
        colors = TimePickerDefaults.colors(
            containerColor = LocalCustomColorsPalette.current.elevationBackground,
        ),
    )
}
