package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import band.effective.office.shared.core.utils.asInstant
import band.effective.office.shared.core.utils.asLocalDateTime
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerView(
    modifier: Modifier = Modifier,
    currentDate: LocalDateTime,
    onChangeDate: (LocalDate) -> Unit,
) {
    // Create and remember the date picker state
    val state = rememberAdaptiveDatePickerState(
        initialSelectedDateMillis = currentDate.asInstant.toEpochMilliseconds(),
    )

    // React to date changes
    LaunchedEffect(state.selectedDateMillis) {
        val selectedDate = state.selectedDateMillis?.let { millis ->
            Instant.fromEpochMilliseconds(millis).asLocalDateTime.date
        }

        selectedDate?.let {
            val year = it.year
            val month = it.month
            val day = it.dayOfMonth
            onChangeDate(LocalDate(year, month, day))
        }
    }

    val palette = LocalCustomColorsPalette.current
    val accent = MaterialTheme.colorScheme.primary
    val onAccent = MaterialTheme.colorScheme.onPrimary

    Column {
        AdaptiveDatePicker(
            state = state,
            modifier = modifier,
            colors = DatePickerDefaults.colors(
                containerColor = palette.elevationBackground,
                titleContentColor = palette.primaryTextAndIcon,
                headlineContentColor = palette.primaryTextAndIcon,
                weekdayContentColor = palette.secondaryTextAndIcon,
                subheadContentColor = palette.primaryTextAndIcon,
                navigationContentColor = accent,
                yearContentColor = palette.primaryTextAndIcon,
                currentYearContentColor = accent,
                selectedYearContentColor = onAccent,
                selectedYearContainerColor = accent,
                dayContentColor = palette.primaryTextAndIcon,
                selectedDayContentColor = onAccent,
                selectedDayContainerColor = accent,
                todayContentColor = accent,
                todayDateBorderColor = accent,
            ),
        )
    }
}
