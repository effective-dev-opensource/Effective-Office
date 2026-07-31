package io.github.aakira.napier

// Napier не публикует linux-таргеты, поэтому под Аврору занимаем его пакет заглушкой:
// вызывающий код остаётся без изменений, логи уходят в stdout. Подключается только
// aurora-вариантом (kotlin.srcDir в build.aurora.gradle.kts) и только здесь — в одном
// модуле, иначе получим дублирующиеся символы в klib. До планшетных модулей доходит
// транзитивно: core/domain и core/ui делают api(project(":clients:shared:core")).

enum class LogLevel { VERBOSE, DEBUG, INFO, WARNING, ERROR, ASSERT }

// Под journald (и вообще под пайпом) stdout буферизуется целиком, поэтому строки,
// напечатанные перед падением, не доезжают. Сбрасываем после каждой.
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
