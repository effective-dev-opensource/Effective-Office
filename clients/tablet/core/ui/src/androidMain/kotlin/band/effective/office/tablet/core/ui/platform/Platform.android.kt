package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual val forceLandscape: Boolean = false

actual val statusBarInset: Dp = 0.dp

actual val popupIsSeparateScene: Boolean = false

// The system provides the density — scaling is off.
actual val uiScaleBaseline: Dp = 0.dp

@Composable
actual fun softKeyboardOverlapPx(): Int {
    val density = LocalDensity.current
    val ime = WindowInsets.ime.getBottom(density)
    val navigationBars = WindowInsets.navigationBars.getBottom(density)
    return maxOf(0, ime - navigationBars)
}

// The ime goes down with the focus by itself.
actual fun closeSoftKeyboard() = Unit

// The ime inset arrives on its own, in step with the animation — nothing to be warned about.
actual fun noteSoftKeyboardExpected() = Unit

@Composable
actual fun listFlingBehavior(): FlingBehavior = ScrollableDefaults.flingBehavior()

/** Nothing to re-apply: the frame is a no-op here, so a dialog inherits everything worth having. */
@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) = content()
