package band.effective.office.tablet.feature.main.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h2
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.success_text
import org.jetbrains.compose.resources.stringResource

@Composable
fun SuccessText(modifier: Modifier, nameRoom: String) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.success_text, nameRoom),
            style = MaterialTheme.typography.h2,
            color = LocalCustomColorsPalette.current.primaryTextAndIcon
        )
    }
}