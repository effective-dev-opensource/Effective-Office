package band.effective.office.tablet.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.office.tablet.BuildKonfig
import androidx.compose.foundation.layout.BoxScope

@Composable
fun BoxScope.VersionOverlay(
    modifier: Modifier = Modifier,
    text: String = "v${BuildKonfig.VERSION_NAME}",
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
        textAlign = TextAlign.Start,
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(
                end = 40.dp,
                bottom = 10.dp
            )
    )
}

