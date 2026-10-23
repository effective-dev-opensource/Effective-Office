package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Composable
expect fun TimePickerView(
    modifier: Modifier = Modifier,
    currentDate: LocalDateTime,
    onSnap: (LocalTime) -> Unit,
)
