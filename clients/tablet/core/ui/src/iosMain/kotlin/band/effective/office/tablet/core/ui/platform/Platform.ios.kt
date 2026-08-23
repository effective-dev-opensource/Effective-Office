package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getCurrentLanguageCode(): String = NSLocale.currentLocale.languageCode ?: "en"

actual val forceLandscape: Boolean = false

actual val statusBarInset: Dp = 0.dp

actual val uiScaleBaseline: Dp = 0.dp

@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) = content()
