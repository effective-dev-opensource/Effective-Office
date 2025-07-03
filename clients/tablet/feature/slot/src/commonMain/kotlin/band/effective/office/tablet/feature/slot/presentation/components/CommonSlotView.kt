package band.effective.office.tablet.feature.slot.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import band.effective.office.tablet.core.domain.util.toFormattedString
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.core.ui.theme.h7
import band.effective.office.tablet.feature.slot.presentation.SlotUi

@Composable
fun CommonSlotView(
    modifier: Modifier = Modifier,
    slotUi: SlotUi,
    content: @Composable () -> Unit = {}
) {
    val slot = slotUi.slot
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "${slot.start.toFormattedString("HH:mm")} - ${slot.finish.toFormattedString("HH:mm")}",
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = slot.subtitle(),
                style = MaterialTheme.typography.h7,
                color = Color.White
            )
        }
        content()
    }
}