package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Composable
expect fun DatePickerView(
    modifier: Modifier = Modifier,
    currentDate: LocalDateTime,
    onChangeDate: (LocalDate) -> Unit,
)
