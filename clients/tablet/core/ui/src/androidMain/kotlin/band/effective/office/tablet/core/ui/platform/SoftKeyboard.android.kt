package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
actual fun softKeyboardOverlapPx(): Int {
    val density = LocalDensity.current
    val ime = WindowInsets.ime.getBottom(density)
    val navigationBars = WindowInsets.navigationBars.getBottom(density)
    return maxOf(0, ime - navigationBars)
}
