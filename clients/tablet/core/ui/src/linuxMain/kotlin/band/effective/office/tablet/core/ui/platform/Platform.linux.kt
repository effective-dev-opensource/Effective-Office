package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual fun getCurrentLanguageCode(): String = "ru"

actual val forceLandscape: Boolean = true

actual val statusBarInset: Dp = 24.dp

actual val uiScaleBaseline: Dp = 686.dp

@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) {
    AuroraWindowFrame { content() }
}

@Composable
actual fun softKeyboardOverlapPx(): Int = auroraKeyboardOverlapPx()

actual fun noteSoftKeyboardExpected() = noteAuroraKeyboardExpected()
