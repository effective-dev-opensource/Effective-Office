package band.effective.office.tablet.core.ui.inactivity

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

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

    private val _timeouts = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Fires once the tablet has sat untouched for the timeout.
     *
     * A flow rather than the single callback it used to be, because more than one layer has to
     * react to the same tick: the main screen goes back to the room the tablet was set up with,
     * and a modal open on top of it has to close. Without the second one the modal stays, still
     * addressing the room it was opened for, over a screen that has already moved on.
     */
    val timeouts: SharedFlow<Unit> = _timeouts.asSharedFlow()

    private var timer: InactivityTimer? = null
    private var job: Job? = null

    /**
     * Starts (or restarts) tracking. Idempotent: calling it again replaces the previous countdown
     * rather than stacking a second one, so a recreated composition does not double-fire.
     */
    fun start(timeout: Duration = DEFAULT_TIMEOUT) {
        stop()
        timer = InactivityTimer(timeout) { _timeouts.tryEmit(Unit) }
            .also { job = it.start(scope) }
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
