package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.minus_date_button_string
import band.effective.office.tablet.core.ui.plus_date_button_string
import band.effective.office.tablet.core.ui.select_length_title
import band.effective.office.tablet.core.ui.short_hours
import band.effective.office.tablet.core.ui.short_minuets
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h4
import band.effective.office.tablet.core.ui.theme.h6
import band.effective.office.tablet.core.ui.theme.h8
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventDurationView(
    modifier: Modifier = Modifier,
    currentDuration: Int,
    increment: () -> Unit,
    decrement: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.select_length_title),
            color = LocalCustomColorsPalette.current.secondaryTextAndIcon,
            style = MaterialTheme.typography.h8
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier.fillMaxHeight().weight(1f).clip(RoundedCornerShape(15.dp)),
                onClick = { decrement() },
                enabled = currentDuration > 15,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColorsPalette.current.elevationBackground
                )
            ) {
                Text(
                    text = stringResource(Res.string.minus_date_button_string),
                    style = MaterialTheme.typography.h6
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = currentDuration.getDurationString(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier.fillMaxHeight().weight(1f).clip(RoundedCornerShape(15.dp)),
                onClick = {
                    increment()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalCustomColorsPalette.current.elevationBackground
                )
            ) {
                Text(
                    text = stringResource(Res.string.plus_date_button_string),
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }

}

@Composable
private fun Int.getDurationString(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours == 0 -> "$minutes${stringResource(Res.string.short_minuets)}"
        minutes == 0 -> "$hours${stringResource(Res.string.short_hours)}"
        else -> "$hours${stringResource( Res.string.short_hours)} " +
                "$minutes${stringResource(Res.string.short_minuets)}"
    }
}
