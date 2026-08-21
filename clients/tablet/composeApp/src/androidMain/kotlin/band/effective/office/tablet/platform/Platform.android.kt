package band.effective.office.tablet.platform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tablet.BuildConfig

actual val isDebug: Boolean = BuildConfig.DEBUG

@Composable
actual fun ModalBackHandler(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}

actual fun Modifier.modalKeyboardPadding(): Modifier = imePadding()
