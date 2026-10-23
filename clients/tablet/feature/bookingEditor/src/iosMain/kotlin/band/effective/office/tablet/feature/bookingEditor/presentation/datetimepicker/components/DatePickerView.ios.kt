package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun DatePickerView(
    modifier: Modifier,
    currentDate: LocalDateTime,
    onChangeDate: (LocalDate) -> Unit,
) {
    val state = rememberAdaptiveDatePickerState(
        initialSelectedDateMillis = currentDate.date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
    )

    LaunchedEffect(state.selectedDateMillis) {
        val selectedDate = state.selectedDateMillis?.let { millis ->
            Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
        }

        selectedDate?.let { onChangeDate(it) }
    }

    Column {
        AdaptiveDatePicker(
            state = state,
            modifier = modifier,
            colors = DatePickerDefaults.colors(
                containerColor = LocalCustomColorsPalette.current.elevationBackground,
            ),
        )
    }
}
