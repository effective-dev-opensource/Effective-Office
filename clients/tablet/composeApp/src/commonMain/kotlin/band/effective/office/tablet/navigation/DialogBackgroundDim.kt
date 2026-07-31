package band.effective.office.tablet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import band.effective.office.tablet.core.ui.platform.ForcedLandscape
import band.effective.office.tablet.core.ui.platform.ScaledUiDensity

/**
 * Full-screen dim (matching the pre-navigation-swap Decompose overlay of `Color.Black` at 0.9 alpha)
 * behind a centered modal. Tapping the dim area dismisses; taps on the content are absorbed. Used by
 * every `dialog<>` destination in [AppNavHost], which sets `usePlatformDefaultWidth = false` so the
 * dialog window can fill the screen and let this scrim draw edge to edge.
 */
@Composable
fun DialogBackgroundDim(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    ForcedLandscape {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
            ) {
                ScaledUiDensity {
                    content()
                }
            }
        }
    }
}
