package band.effective.office.tablet.platform

import androidx.compose.runtime.Composable
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean = Platform.isDebugBinary

actual val showsDebugMetrics: Boolean = isDebug

// No back gesture on iPad: the modal is dismissed by its cross button or a tap on the dim.
@Composable
actual fun ModalBackHandler(onBack: () -> Unit) = Unit
