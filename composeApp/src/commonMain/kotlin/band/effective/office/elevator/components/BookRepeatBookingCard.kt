package band.effective.office.elevator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun BookRepeatBookingCard(
    modifier: Modifier = Modifier,
    zone: String,
    table: String,
    dateRange: String,
    time: String,
    isActive: Boolean = false
) {
    var showInfo by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EffectiveTheme.colors.card.active,
        ),
        border = BorderStroke(2.dp, EffectiveTheme.colors.stroke.primary)
    ) {
        Row(
            modifier = modifier.padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    modifier = modifier.padding(top = 16.dp, start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isActive) {
                        Icon(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 4.dp),
                            painter = painterResource(MainRes.images.ic_repeat),
                            contentDescription = "Active",
                            tint = EffectiveTheme.colors.graph.violet,
                        )
                    }

                    Text(
                        text = "$zone, $table",
                        style = EffectiveTheme.typography.mMedium,
                        color = if (isActive) EffectiveTheme.colors.text.optional else EffectiveTheme.colors.text.primary
                    )

                    if (isActive) {
                        Icon(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(start = 4.dp),
                            painter = painterResource(MainRes.images.ic_repeat),
                            contentDescription = "Active",
                            tint = EffectiveTheme.colors.graph.violet,
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(bottom = 16.dp, start = 16.dp),
                    text = "$dateRange, $time",
                    style = EffectiveTheme.typography.mMedium,
                    color = EffectiveTheme.colors.text.secondary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconWithRepeatBookingInfo(
                onClick = { showInfo = !showInfo },
                showInfo = showInfo,
                onDismissRequest = { showInfo = false },
                repeatDaysText = dateRange,
                untilDateText = dateRange
            )
            ActionMenu()
        }
    }
}