package io.github.aakira.napier

// Napier publishes no linux targets, so the aurora variant squats its package and call sites keep
// their imports untouched. Exactly one module may carry the stub: a second copy of the package
// collides at klib link time, so core:domain and core:ui re-export this module instead.

enum class LogLevel { VERBOSE, DEBUG, INFO, WARNING, ERROR, ASSERT }

// stdout is fully buffered under journald, so lines printed right before a crash never get out.
internal expect fun flushLog()

open class Antilog {
    open fun log(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
        println(
            buildString {
                append(priority.name)
                if (tag != null) append("/$tag")
                if (message != null) append(": $message")
                if (throwable != null) append(" | ${throwable.stackTraceToString()}")
            },
        )
        flushLog()
    }
}

class DebugAntilog : Antilog()

object Napier {
    private var antilog: Antilog? = null

    fun base(antilog: Antilog) {
        this.antilog = antilog
    }

    fun log(priority: LogLevel, tag: String? = null, throwable: Throwable? = null, message: String) =
        antilog?.log(priority, tag, throwable, message)

    fun v(message: String, throwable: Throwable? = null, tag: String? = null) =
        log(LogLevel.VERBOSE, tag, throwable, message)

    fun v(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        log(LogLevel.VERBOSE, tag, throwable, message())

    fun d(message: String, throwable: Throwable? = null, tag: String? = null) =
        log(LogLevel.DEBUG, tag, throwable, message)

    fun d(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        log(LogLevel.DEBUG, tag, throwable, message())

    fun i(message: String, throwable: Throwable? = null, tag: String? = null) =
        log(LogLevel.INFO, tag, throwable, message)

    fun i(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        log(LogLevel.INFO, tag, throwable, message())

    fun w(message: String, throwable: Throwable? = null, tag: String? = null) =
        log(LogLevel.WARNING, tag, throwable, message)

    fun w(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        log(LogLevel.WARNING, tag, throwable, message())

    fun e(message: String, throwable: Throwable? = null, tag: String? = null) =
        log(LogLevel.ERROR, tag, throwable, message)

    fun e(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        log(LogLevel.ERROR, tag, throwable, message())

    fun wtf(message: String, throwable: Throwable? = null, tag: String? = null) =
        log(LogLevel.ASSERT, tag, throwable, message)

    fun wtf(throwable: Throwable? = null, tag: String? = null, message: () -> String) =
        log(LogLevel.ASSERT, tag, throwable, message())
}
