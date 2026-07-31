package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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

    val palette = LocalCustomColorsPalette.current
    val accent = MaterialTheme.colorScheme.primary
    val onAccent = MaterialTheme.colorScheme.onPrimary

    TimePicker(
        state = state,
        modifier = modifier,
        layoutType = TimePickerLayoutType.Vertical,
        colors = TimePickerDefaults.colors(
            containerColor = palette.elevationBackground,
            clockDialColor = MaterialTheme.colorScheme.surface,
            selectorColor = accent,
            clockDialSelectedContentColor = onAccent,
            clockDialUnselectedContentColor = palette.primaryTextAndIcon,
            periodSelectorSelectedContainerColor = accent,
            periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
            periodSelectorSelectedContentColor = onAccent,
            periodSelectorUnselectedContentColor = palette.primaryTextAndIcon,
            timeSelectorSelectedContainerColor = accent,
            timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
            timeSelectorSelectedContentColor = onAccent,
            timeSelectorUnselectedContentColor = palette.primaryTextAndIcon,
        ),
    )
}
