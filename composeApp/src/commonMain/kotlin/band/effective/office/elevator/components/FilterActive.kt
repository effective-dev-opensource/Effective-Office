package band.effective.office.elevator.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun FilterActive(
    modifier: Modifier = Modifier,
    textFilter: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = EffectiveTheme.colors.button.primaryNormal),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
                .padding(
                    horizontal = 8.dp,
                    vertical = 5.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = textFilter,
                style = EffectiveTheme.typography.sMedium,
                color = EffectiveTheme.colors.text.primaryEvent,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Divider(
                modifier = Modifier.fillMaxHeight().width(1.dp),
                color = EffectiveTheme.colors.icon.primaryEvent
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 1.dp),
                onClick = onClick,
            ) {
                Icon(
                    painter = painterResource(MainRes.images.ic_cross),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = EffectiveTheme.colors.icon.primaryEvent,
                )
            }
        }
    }
}
