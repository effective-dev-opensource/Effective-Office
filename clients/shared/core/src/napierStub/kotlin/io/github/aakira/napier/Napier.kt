package io.github.aakira.napier

// Napier publishes no linux targets, so on Aurora we squat its package with a stub: callers
// stay untouched and the logs go to stdout. Wired in by the aurora variant only (kotlin.srcDir
// in build.aurora.gradle.kts) and only here — in a single module, or the klib link ends up with
// duplicate symbols. The tablet modules get it transitively: core/domain and core/ui declare
// api(project(":clients:shared:core")).

enum class LogLevel { VERBOSE, DEBUG, INFO, WARNING, ERROR, ASSERT }

// Under journald (and under a pipe in general) stdout is fully buffered, so lines printed
// right before a crash never make it out. Flush after every one.
internal expect fun flushLog()

open class Antilog {
    open fun log(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
        println(buildString {
            append(priority.name)
            if (tag != null) append("/$tag")
            if (message != null) append(": $message")
            if (throwable != null) append(" | ${throwable.stackTraceToString()}")
        })
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
