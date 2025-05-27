package band.effective.office.elevator.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NotificationCard(
    modifier: Modifier = Modifier,
    cardTitle: String,
    cardDescription: String?,
    onCloseClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = EffectiveTheme.colors.background.primary,
        ),
        border = BorderStroke(1.dp, EffectiveTheme.colors.stroke.primary),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(MainRes.images.icon_success),
                        contentDescription = null,
                        tint = EffectiveTheme.colors.icon.success,
                    )
                    Text(
                        text = cardTitle,
                        textAlign = TextAlign.Start,
                        style = EffectiveTheme.typography.mMedium,
                        color = EffectiveTheme.colors.text.primary,
                    )
                }
                if (cardDescription != null) {
                    Text(
                        text = cardDescription,
                        textAlign = TextAlign.Start,
                        style = EffectiveTheme.typography.mMedium,
                        color = EffectiveTheme.colors.text.secondary,
                    )
                }
            }
            IconButton(
                modifier = Modifier.size(16.dp),
                onClick = onCloseClick,
            ) {
                Icon(
                    modifier = Modifier.size(10.dp),
                    painter = painterResource(MainRes.images.ic_close),
                    tint = EffectiveTheme.colors.icon.secondary,
                    contentDescription = stringResource(MainRes.strings.close)
                )
            }
        }
    }
}
