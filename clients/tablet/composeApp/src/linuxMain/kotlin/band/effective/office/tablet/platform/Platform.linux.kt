package band.effective.office.tablet.platform

import androidx.compose.runtime.Composable
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

// The Aurora variant links a release executable, so this is always false — and it is the same flag
// that picks API_URL_RELEASE.
@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean = Platform.isDebugBinary

// Not isDebug: that would hide the metrics on the one platform they are being added for.
actual val showsDebugMetrics: Boolean = true

// Aurora's back gesture is not routed to the app yet; the modal closes by its cross button.
@Composable
actual fun ModalBackHandler(onBack: () -> Unit) = Unit
