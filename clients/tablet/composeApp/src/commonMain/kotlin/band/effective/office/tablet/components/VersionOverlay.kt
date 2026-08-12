package band.effective.office.tablet.components

import androidx.compose.foundation.layout.BoxScope
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

/**
 * The build's version, in the bottom corner, on every build and every platform.
 *
 * It is there to answer "which build is this one?" of a tablet on a wall, which is a question
 * asked of a released device rather than a debug one — so no debug gate.
 *
 * It used to carry the window size, the system density and the density `ScaledUiDensity` had
 * computed, which is how the 686 dp baseline was measured. That was scaffolding and it is gone;
 * `ScaledUiDensity` still logs its own answer under the `UiScale` tag if the numbers are wanted
 * again.
 */
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
