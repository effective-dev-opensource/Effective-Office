package band.effective.office.shared.core.crashlytics

import android.content.Context
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking

class OfficeUncaughtExceptionHandler(
    private val context: Context,
    private val enabled: Boolean,
    private val crashApi: SendCrashApi,
    private val handler: Thread.UncaughtExceptionHandler? = null
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(p0: Thread, p1: Throwable) {
        if (!enabled) {
            Napier.i { "Handled uncaught exception disable" }
            return
        }
        Napier.e {
            """
            Handled uncaught exception
            ${p1.stackTraceToString()}
        """.trimIndent()
        }
        runBlocking { crashApi.send(p1.stackTraceToString(), context.packageName) }
            .onFailure { Napier.e("Fail send crash", it) }
            .onSuccess { Napier.i { "Crash sent completed" } }
        handler?.uncaughtException(p0, p1)
    }
}