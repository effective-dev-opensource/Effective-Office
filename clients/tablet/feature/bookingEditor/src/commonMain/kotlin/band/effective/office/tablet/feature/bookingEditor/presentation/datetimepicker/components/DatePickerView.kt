package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import kotlin.time.Instant
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
            val day = it.day
            onChangeDate(LocalDate(year, month.ordinal, day))
        }
    }

    Column {
        // Display the date picker
        AdaptiveDatePicker(
            state = state,
            modifier = modifier,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}
