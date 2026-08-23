package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Everything the Aurora window needs doing to it before an app can be laid out inside. The order
 * of the wrappers is not free, and the theme belongs outside them — see "Aurora window model" in
 * clients/tablet/core/ui/README.md. Only Aurora has anything to apply here.
 */
@Composable
fun AuroraWindowFrame(content: @Composable () -> Unit) {
    ForcedLandscape {
        ScaledUiDensity(modifier = Modifier.fillMaxSize()) {
            if (statusBarInset <= 0.dp) {
                content()
                return@ScaledUiDensity
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = statusBarInset),
                contentAlignment = Alignment.Center,
            ) {
                content()
            }
        }
    }
}
