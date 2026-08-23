package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

actual fun getCurrentLanguageCode(): String = Locale.getDefault().language

actual val forceLandscape: Boolean = false

actual val statusBarInset: Dp = 0.dp

actual val uiScaleBaseline: Dp = 0.dp

@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) = content()
