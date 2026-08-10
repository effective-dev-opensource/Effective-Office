package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import kotlin.math.min

/**
 * Normalises the content's dp space to [uiScaleBaseline] by the short side: substitutes
 * [LocalDensity] with `available_short_side_px / uiScaleBaseline`. A layout written in dp then
 * occupies the same fraction of the screen whatever scale the system handed over. Where scaling
 * is off (Android, iOS) this is a no-op.
 *
 * `fontScale` is pinned to 1: otherwise the system font scale would multiply on top of ours and
 * drift the text away from the layout.
 *
 * The size comes from our own constraints rather than `LocalWindowInfo.containerSize`, so that
 * already-subtracted padding (the status bar) is accounted for and a dialog window behaves the
 * same way.
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
        // The scale follows the scene's short side, so a scene that shrinks — for a keyboard, say —
        // takes the whole layout down with it, which looks like the content squeezing rather than
        // moving. Logged on every change, because that would otherwise be invisible from outside.
        LaunchedEffect(constraints.maxWidth, constraints.maxHeight) {
            Napier.i(tag = "UiScale") {
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
