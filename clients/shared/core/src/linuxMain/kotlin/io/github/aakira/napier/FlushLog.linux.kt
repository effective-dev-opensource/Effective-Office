package io.github.aakira.napier

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fflush

// `fflush(null)` сбрасывает все открытые потоки — этого достаточно и не требует ссылки на stdout.
@OptIn(ExperimentalForeignApi::class)
internal actual fun flushLog() {
    fflush(null)
}
