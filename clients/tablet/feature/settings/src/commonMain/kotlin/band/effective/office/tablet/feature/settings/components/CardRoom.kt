package band.effective.office.tablet.feature.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h4

@Composable
fun CardRoom(
    modifier: Modifier,
    nameRoom: String,
    currentNameRoom: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier.scale(1.4f),
                selected = nameRoom == currentNameRoom,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = LocalCustomColorsPalette.current.secondaryTextAndIcon
                )
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = nameRoom,
                style = MaterialTheme.typography.h4,
                color = LocalCustomColorsPalette.current.primaryTextAndIcon
            )
        }
    }
}