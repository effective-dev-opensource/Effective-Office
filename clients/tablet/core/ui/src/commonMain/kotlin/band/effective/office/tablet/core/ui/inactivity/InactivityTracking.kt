package band.effective.office.tablet.core.ui.inactivity

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Process-wide inactivity countdown, fed by [InactivityTracker] through [onUserInteraction].
 */
object InactivityTracking {
    /** How long the tablet may sit untouched before the screen is reset. */
    val DEFAULT_TIMEOUT: Duration = 1.minutes

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var timer: InactivityTimer? = null
    private var job: Job? = null

    /** Idempotent: a second call replaces the running countdown instead of stacking another one. */
    fun start(timeout: Duration = DEFAULT_TIMEOUT, onTimeout: () -> Unit) {
        stop()
        timer = InactivityTimer(timeout, onTimeout).also { job = it.start(scope) }
    }

    fun stop() {
        job?.cancel()
        job = null
        timer = null
    }

    fun onUserInteraction() {
        timer?.restart()
    }
}
