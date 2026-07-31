package band.effective.office.tablet

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

// The Aurora variant links a release executable, so this is always false — which also means
// API_URL_RELEASE is used. Worth remembering when a build talks to the wrong backend.
@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean = Platform.isDebugBinary
