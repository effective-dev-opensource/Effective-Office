package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity

/**
 * Rotates the content to landscape when the window arrived portrait and [forceLandscape] is on:
 * a landscape-sized box (H×W) is centred and rotated 90° about its own centre, so its footprint
 * after the rotation is exactly W×H and covers the screen. A no-op on a landscape window and on
 * Android and iOS.
 *
 * EVERY layer has to be wrapped: the fork renders `Popup` and `dialog<>` as separate scenes in
 * the untouched window, and nothing applied at the root reaches them.
 */
@Composable
fun ForcedLandscape(content: @Composable () -> Unit) {
    if (!forceLandscape) {
        content()
        return
    }
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        if (w >= h) {
            content()
        } else {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .requiredSize(
                        width = with(density) { h.toDp() },
                        height = with(density) { w.toDp() },
                    )
                    .rotate(90f),
            ) {
                content()
            }
        }
    }
}
