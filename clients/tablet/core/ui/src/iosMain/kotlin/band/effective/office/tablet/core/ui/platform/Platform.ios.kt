package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual val forceLandscape: Boolean = false

actual val popupIsSeparateScene: Boolean = false

actual val uiScaleBaseline: Dp = 0.dp

// UIKit already shortened the scene to the area above the keyboard, so nothing that is left of the
// content is covered.
@Composable
actual fun softKeyboardOverlapPx(): Int = 0

// UIKit takes the keyboard down with the focus by itself.
actual fun closeSoftKeyboard() = Unit
