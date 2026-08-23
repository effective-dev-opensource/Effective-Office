package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

actual fun getCurrentLanguageCode(): String = Locale.getDefault().language

actual val forceLandscape: Boolean = false

actual val statusBarInset: Dp = 0.dp

actual val uiScaleBaseline: Dp = 0.dp

@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) = content()

// The ime inset is measured from the bottom of the window and includes the navigation bar, which
// the content is already padded away from.
@Composable
actual fun softKeyboardOverlapPx(): Int {
    val density = LocalDensity.current
    return maxOf(
        0,
        WindowInsets.ime.getBottom(density) - WindowInsets.navigationBars.getBottom(density),
    )
}

// The ime inset arrives on its own, in step with the animation.
actual fun noteSoftKeyboardExpected() = Unit

// The ime goes down with the focus by itself.
actual fun closeSoftKeyboard() = Unit
