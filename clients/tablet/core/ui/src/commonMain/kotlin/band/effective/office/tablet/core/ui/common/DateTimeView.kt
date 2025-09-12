package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.Res
 import band.effective.office.tablet.core.ui.date_booking
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.core.ui.utils.DateDisplayMapper
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun DateTimeView(
    modifier: Modifier,
    startTime: LocalDateTime,
    finishTime: LocalDateTime,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(
                Res.string.date_booking,
                startTime,
                DateDisplayMapper.formatTime(startTime),
                DateDisplayMapper.formatTime(finishTime)
            ),
            style = MaterialTheme.typography.h5,
            color = LocalCustomColorsPalette.current.primaryTextAndIcon
        )
    }
}