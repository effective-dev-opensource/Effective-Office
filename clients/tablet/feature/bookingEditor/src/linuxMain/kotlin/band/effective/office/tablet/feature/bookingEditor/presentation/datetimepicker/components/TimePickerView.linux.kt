package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60
private const val TIME_PAD_WIDTH = 2
private val CELL_HEIGHT = 40.dp
private val CELL_CORNER_RADIUS = 8.dp
private val COLUMN_SPACING = 8.dp

/**
 * Aurora has no calf, and the hand-rolled month grid next door leaves no reason to reach for
 * Material3 here either. Minute by minute, matching what the Android and iOS pickers allow.
 */
@Composable
actual fun TimePickerView(
    modifier: Modifier,
    currentDate: LocalDateTime,
    onSnap: (LocalTime) -> Unit,
) {
    var hour by remember { mutableStateOf(currentDate.hour) }
    var minute by remember { mutableStateOf(currentDate.minute) }

    LaunchedEffect(hour, minute) { onSnap(LocalTime(hour, minute, 0)) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(COLUMN_SPACING),
    ) {
        TimeColumn(
            modifier = Modifier.weight(1f),
            values = 0 until HOURS_IN_DAY,
            selected = hour,
            onSelect = { hour = it },
        )
        TimeColumn(
            modifier = Modifier.weight(1f),
            values = 0 until MINUTES_IN_HOUR,
            selected = minute,
            onSelect = { minute = it },
        )
    }
}

@Composable
private fun TimeColumn(
    modifier: Modifier,
    values: IntRange,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val palette = LocalCustomColorsPalette.current
    val accent = MaterialTheme.colorScheme.primary
    val onAccent = MaterialTheme.colorScheme.onPrimary
    val scrollState = rememberScrollState()
    val cellHeightPx = with(LocalDensity.current) { CELL_HEIGHT.roundToPx() }

    LaunchedEffect(Unit) { scrollState.scrollTo(selected * cellHeightPx) }

    Column(modifier = modifier.verticalScroll(scrollState)) {
        values.forEach { value ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CELL_HEIGHT)
                    .background(
                        color = if (isSelected) accent else palette.elevationBackground,
                        shape = RoundedCornerShape(CELL_CORNER_RADIUS),
                    )
                    .clickable { onSelect(value) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = value.toString().padStart(TIME_PAD_WIDTH, '0'),
                    color = if (isSelected) onAccent else palette.primaryTextAndIcon,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
