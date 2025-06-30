package band.effective.office.tablet.core.ui.date

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_left
import band.effective.office.tablet.core.ui.arrow_right
import band.effective.office.tablet.core.ui.select_date_tine_title
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h6
import band.effective.office.tablet.core.ui.theme.h8
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalTime::class)
@Composable
fun DateTimeView(
    modifier: Modifier,
    selectDate: Instant,
    currentDate: Instant? = null,
    back: () -> Unit = {},
    increment: () -> Unit,
    decrement: () -> Unit,
    onOpenDateTimePickerModal: () -> Unit,
    showTitle: Boolean = false
) {
    val backButtonWeight = when {
        currentDate == null -> 0f
        currentDate != selectDate -> 1.5f
        else -> 0f
    }
    val timeDayMonthDateFormat = remember { dateTimeFormat }
    val dayMonthDateFormat = remember { dayMonthFormat }

    Column(modifier = modifier) {
        if (showTitle) {
            Text(
                text = stringResource(Res.string.select_date_tine_title),
                color = LocalCustomColorsPalette.current.secondaryTextAndIcon,
                style = MaterialTheme.typography.h8
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Row {
            Button(
                modifier = Modifier.fillMaxHeight().weight(1f).clip(RoundedCornerShape(15.dp)),
                onClick = { decrement() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColorsPalette.current.elevationBackground
                )
            ) {
                Image(
                    modifier = Modifier,
                    painter = painterResource(Res.drawable.arrow_left),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.fillMaxHeight().weight(4f - backButtonWeight)
                    .clip(RoundedCornerShape(15.dp)),
                onClick = { onOpenDateTimePickerModal() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColorsPalette.current.elevationBackground
                )
            ) {
                Text(
                    text = timeDayMonthDateFormat.format(selectDate.toLocalDateTime(TimeZone.currentSystemDefault())),
                    style = MaterialTheme.typography.h6
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (backButtonWeight > 0) {
                Button(
                    modifier = Modifier.fillMaxHeight().weight(backButtonWeight)
                        .clip(RoundedCornerShape(15.dp))
                        .border(3.dp, Color.White, RoundedCornerShape(15.dp)),
                    onClick = back,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalCustomColorsPalette.current.elevationBackground
                    )
                ) {
                    Text(
                        text = dayMonthDateFormat.format(currentDate?.toLocalDateTime(TimeZone.UTC)!!),
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Button(
                modifier = Modifier.fillMaxHeight().weight(1f).clip(RoundedCornerShape(15.dp)),
                onClick = { increment() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColorsPalette.current.elevationBackground
                )
            ) {
                Image(
                    modifier = Modifier,
                    painter = painterResource(Res.drawable.arrow_right),
                    contentDescription = null
                )
            }
        }
    }
}
