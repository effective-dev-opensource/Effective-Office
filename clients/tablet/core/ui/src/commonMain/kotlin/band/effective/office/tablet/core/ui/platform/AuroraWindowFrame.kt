package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Everything the Aurora window needs doing to it before an app can be laid out inside: the content
 * is rotated to landscape, Aurora's status bar is padded away, and the dp space is normalised.
 *
 * One composable rather than three because the order between them is not free and gets forgotten
 * when they are written out by hand:
 *
 * - the **inset goes inside the rotation.** Outside it, the padding lands in the window's portrait
 *   coordinate space and shows up as a stripe down the edge instead of a band along the top;
 * - the **inset goes inside the scale**, not the other way round. [ScaledUiDensity] measures its
 *   own constraints, so putting it under the padding would have it normalise 1157 px rather than
 *   the window's 1200 — and the whole point of `uiScaleBaseline` is that 1200/686 is exactly the
 *   reference Android tablet's 1.75. Measured against the padded height the parity is gone and
 *   everything comes out ~3.6% larger. So the dp space is fixed against the window, and the status
 *   bar is padded away inside it.
 *
 * A no-op on Android and iOS: `forceLandscape` is false, `uiScaleBaseline` is `0.dp` and
 * `statusBarInset` is `0.dp` there, so all three layers pass their content straight through. That
 * is why the frame can be wrapped around the linux entry point alone and left off the other two
 * ([DialogSceneFrame] is the same idea for content that gets a scene of its own).
 *
 * Deliberately no background: `AppTheme`'s own `Surface` paints one, and the entry points apply
 * the theme *around* this frame precisely so that it reaches the strip the inset leaves bare. An
 * earlier arrangement had the theme inside and the strip came out white — the bare window is not
 * dark, whatever one might assume.
 */
@Composable
fun AuroraWindowFrame(content: @Composable () -> Unit) {
    ForcedLandscape {
        ScaledUiDensity(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = statusBarInset),
            ) {
                content()
            }
        }
    }
}
