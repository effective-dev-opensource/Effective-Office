package band.effective.office.tablet.core.ui.inactivity

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Process-wide inactivity countdown, driven by the UI through [onUserInteraction].
 *
 * A room tablet returns to the room it was set up with when nobody has touched it for a while;
 * the countdown lives here, next to [DateResetManager], which is what it usually ends up calling.
 */
object InactivityTracking {
    /** How long the tablet may sit untouched before the screen is reset. */
    val DEFAULT_TIMEOUT: Duration = 1.minutes

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var timer: InactivityTimer? = null
    private var job: Job? = null

    /**
     * Starts (or restarts) tracking. Idempotent: calling it again replaces the previous countdown
     * rather than stacking a second one, so a recreated composition does not double-fire.
     */
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
