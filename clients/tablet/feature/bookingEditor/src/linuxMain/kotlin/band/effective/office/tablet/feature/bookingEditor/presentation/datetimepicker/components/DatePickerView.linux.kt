package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.shared.core.utils.MONTHS_RU_NOMINATIVE
import band.effective.office.shared.core.utils.WEEKDAYS_RU_SHORT
import band.effective.office.shared.core.utils.calendarMonthGrid
import band.effective.office.shared.core.utils.plusMonths
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_left
import band.effective.office.tablet.core.ui.arrow_right
import band.effective.office.tablet.core.ui.res.painterResource
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import androidx.compose.foundation.Image
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Aurora's date picker, written by hand.
 *
 * Material3's own DatePicker cannot be used here: the fork ships `PlatformDateFormat` as a stub
 * (`firstDayOfWeek = 0`, `weekdayNames = emptyList()`, `formatWithSkeleton` returning ""), and
 * `DatePicker` walks `firstDayOfWeek - 1 until weekdayNames.size`, which indexes -1 on an empty
 * list and throws on the first frame. The fork swallows that exception and rolls the frame back,
 * so it presents as the dialog hanging and then dying. Patching the index would not be enough
 * either — the month headline would still be empty and the grid still shifted.
 *
 * Deliberately no LazyVerticalGrid and no FlowRow: both are SubcomposeLayout, which this dialog
 * avoids on purpose (see DateTimePicker). A fixed 6x7 of Columns and Rows needs nothing beyond
 * compose.foundation.
 */
@Composable
actual fun DatePickerView(
    modifier: Modifier,
    currentDate: LocalDateTime,
    onChangeDate: (LocalDate) -> Unit,
) {
    var visibleMonth by remember { mutableStateOf(currentDate.date) }
    var selected by remember { mutableStateOf(currentDate.date) }

    // Fires on first composition as well as on every pick — and the first emission is load-bearing:
    // it is what primes the picker's own confirm button (see DateTimePickerComponent).
    LaunchedEffect(selected) { onChangeDate(selected) }

    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val palette = LocalCustomColorsPalette.current

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.clickable { visibleMonth = visibleMonth.plusMonths(-1) },
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = null,
            )
            Text(
                text = "${MONTHS_RU_NOMINATIVE[visibleMonth.monthNumber - 1]} ${visibleMonth.year}",
                color = palette.primaryTextAndIcon,
                style = MaterialTheme.typography.titleMedium,
            )
            Image(
                modifier = Modifier.clickable { visibleMonth = visibleMonth.plusMonths(1) },
                painter = painterResource(Res.drawable.arrow_right),
                contentDescription = null,
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            WEEKDAYS_RU_SHORT.forEach { name ->
                Text(
                    modifier = Modifier.weight(1f),
                    text = name,
                    color = palette.secondaryTextAndIcon,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        calendarMonthGrid(visibleMonth.year, visibleMonth.monthNumber).weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        modifier = Modifier.weight(1f),
                        date = date,
                        isSelected = date != null && date == selected,
                        isToday = date != null && date == today,
                        onClick = { date?.let { selected = it } },
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    modifier: Modifier,
    date: LocalDate?,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalCustomColorsPalette.current
    val accent = MaterialTheme.colorScheme.primary
    val onAccent = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(if (isSelected) Modifier.background(accent, CircleShape) else Modifier)
            .then(if (isToday && !isSelected) Modifier.border(1.dp, accent, CircleShape) else Modifier)
            .then(if (date != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (date != null) {
            Text(
                text = date.day.toString(),
                color = if (isSelected) onAccent else palette.primaryTextAndIcon,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
