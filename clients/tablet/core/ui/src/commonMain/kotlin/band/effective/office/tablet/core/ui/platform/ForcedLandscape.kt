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
 * Turns a portrait window landscape by centring a landscape-sized box and rotating it 90° about
 * its own centre; passes the content straight through where [forceLandscape] is off or the window
 * is landscape already. See "Aurora window model" in clients/tablet/core/ui/README.md.
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
        val windowWidth = constraints.maxWidth
        val windowHeight = constraints.maxHeight
        if (windowWidth >= windowHeight) {
            content()
        } else {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .requiredSize(
                        width = with(density) { windowHeight.toDp() },
                        height = with(density) { windowWidth.toDp() },
                    )
                    .rotate(90f),
            ) {
                content()
            }
        }
    }
}
