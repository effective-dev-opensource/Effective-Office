package band.effective.office.tablet.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.settings.generated.resources.Res
import band.effective.office.settings.generated.resources.exit
import band.effective.office.tablet.core.ui.exit
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource


@Composable
fun ExitButtonView(
    modifier: Modifier,
    onExitApp: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(0.15f),
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 10.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
            onClick = {
                onExitApp()
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = vectorResource(band.effective.office.tablet.core.ui.Res.drawable.exit),
                        contentDescription = "exit",
                        modifier = Modifier.size(30.dp),
                        tint = LocalCustomColorsPalette.current.tertiaryTextAndIcon
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(Res.string.exit),
                        style = MaterialTheme.typography.h5,
                        color = LocalCustomColorsPalette.current.tertiaryTextAndIcon
                    )
                }
            }
        }
    }
}