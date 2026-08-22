package io.github.aakira.napier

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fflush

@OptIn(ExperimentalForeignApi::class)
internal actual fun flushLog() {
    fflush(null)
}
