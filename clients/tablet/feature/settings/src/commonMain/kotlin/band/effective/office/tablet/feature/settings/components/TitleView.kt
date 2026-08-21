package band.effective.office.tablet.feature.settings.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h2
import band.effective.office.tablet.feature.settings.Res
import band.effective.office.tablet.feature.settings.rooms
import org.jetbrains.compose.resources.stringResource

@Composable
fun TitleView(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(Res.string.rooms),
        style = MaterialTheme.typography.h2,
        color = LocalCustomColorsPalette.current.primaryTextAndIcon
    )
}