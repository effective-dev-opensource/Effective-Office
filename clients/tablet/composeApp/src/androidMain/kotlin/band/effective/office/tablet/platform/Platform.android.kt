package band.effective.office.tablet.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import band.effective.office.tablet.BuildConfig

actual val isDebug: Boolean = BuildConfig.DEBUG

actual val showsDebugMetrics: Boolean = isDebug

@Composable
actual fun ModalBackHandler(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}
