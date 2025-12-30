package band.effective.office.tv.logger

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

class LoggerInitializer {

    fun init(isDebug: Boolean) {
        if (isDebug) {
            Napier.base(DebugAntilog())
        } else {
            Napier.base(ReleaseAntilog)
        }
    }

    private object ReleaseAntilog : Antilog() {
        override fun performLog(
            priority: LogLevel,
            tag: String?,
            throwable: Throwable?,
            message: String?,
        ) {
            if (priority < LogLevel.ERROR) return

            // Build a compact, readable log entry.
            val out = buildString {
                append("ERROR: ")
                tag?.let { append('[').append(it).append("] ") }
                if (!message.isNullOrEmpty()) append(message)
                throwable?.let {
                    appendLine()
                    append(it.stackTraceToString())
                }
            }

            println(out)
        }
    }
}

