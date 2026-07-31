package io.github.aakira.napier

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fflush

// `fflush(null)` flushes every open stream, which is enough here and needs no stdout handle.
@OptIn(ExperimentalForeignApi::class)
internal actual fun flushLog() {
    fflush(null)
}
