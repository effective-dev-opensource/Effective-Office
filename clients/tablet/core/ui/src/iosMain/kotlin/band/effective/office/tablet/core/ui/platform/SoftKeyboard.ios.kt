package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable

// UIKit already shortened the scene to the area above the keyboard, so nothing that is left of the
// content is covered.
@Composable
actual fun softKeyboardOverlapPx(): Int = 0
