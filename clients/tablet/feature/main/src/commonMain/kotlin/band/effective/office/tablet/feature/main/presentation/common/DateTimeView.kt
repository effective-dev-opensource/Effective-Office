package band.effective.office.tablet.feature.main.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.util.timeFormatter
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.date_booking
import kotlinx.datetime.format
import org.jetbrains.compose.resources.stringResource

@Composable
fun DateTimeView(modifier: Modifier, eventInfo: EventInfo) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(
                Res.string.date_booking,
                eventInfo.startTime,
                eventInfo.startTime.format(timeFormatter),
                eventInfo.finishTime.format(timeFormatter)
            ),
            style = MaterialTheme.typography.h5,
            color = LocalCustomColorsPalette.current.primaryTextAndIcon
        )
    }
}