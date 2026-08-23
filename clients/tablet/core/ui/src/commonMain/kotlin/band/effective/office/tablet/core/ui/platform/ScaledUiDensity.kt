package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import kotlin.math.min

private const val UI_SCALE_TAG = "UiScale"

/**
 * Normalises the dp space to [uiScaleBaseline] by the short side of its own constraints, pinning
 * `fontScale` so the system font scale cannot multiply on top. Passes the content through where
 * the baseline is zero — see "Aurora window model" in clients/tablet/core/ui/README.md.
 */
@Composable
fun ScaledUiDensity(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (uiScaleBaseline <= 0.dp) {
        content()
        return
    }
    BoxWithConstraints(modifier = modifier) {
        val shortSidePx = min(constraints.maxWidth, constraints.maxHeight)
        if (!constraints.hasBoundedWidth || !constraints.hasBoundedHeight || shortSidePx <= 0) {
            content()
            return@BoxWithConstraints
        }
        val scaledDensity = shortSidePx / uiScaleBaseline.value
        LaunchedEffect(constraints.maxWidth, constraints.maxHeight) {
            Napier.i(tag = UI_SCALE_TAG) {
                "content ${constraints.maxWidth}x${constraints.maxHeight}, density $scaledDensity"
            }
        }
        CompositionLocalProvider(
            LocalDensity provides Density(density = scaledDensity, fontScale = 1f),
        ) {
            content()
        }
    }
}
