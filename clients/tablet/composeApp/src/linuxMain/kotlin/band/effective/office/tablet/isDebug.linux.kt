package band.effective.office.tablet

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

// Аврорский вариант собирается как release-бинарь, так что здесь всегда false.
// Значит, берётся API_URL_RELEASE — это стоит помнить, если сборка стучится не в тот backend.
@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean = Platform.isDebugBinary
