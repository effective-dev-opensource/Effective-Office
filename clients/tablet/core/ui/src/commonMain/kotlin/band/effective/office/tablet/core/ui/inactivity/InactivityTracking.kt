package band.effective.office.tablet.core.ui.inactivity

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Inactivity countdown, fed by [InactivityTracker] through [onUserInteraction]. The countdown
 * belongs to the caller of [start]: only the token it returned can [stop] it.
 */
class InactivityTracking {

    class Owner internal constructor()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var timer: InactivityTimer? = null
    private var job: Job? = null
    private var owner: Owner? = null

    /** Idempotent: a second call replaces the running countdown instead of stacking another one. */
    fun start(timeout: Duration = DEFAULT_TIMEOUT, onTimeout: () -> Unit): Owner {
        cancel()
        val countdown = InactivityTimer(timeout, onTimeout)
        timer = countdown
        job = countdown.start(scope)
        return Owner().also { owner = it }
    }

    /** No-op unless [owner] is the token of the countdown running right now. */
    fun stop(owner: Owner) {
        if (this.owner !== owner) return
        cancel()
    }

    fun onUserInteraction() {
        timer?.restart()
    }

    private fun cancel() {
        job?.cancel()
        job = null
        timer = null
        owner = null
    }

    companion object {
        /** How long the tablet may sit untouched before the screen is reset. */
        val DEFAULT_TIMEOUT: Duration = 1.minutes
    }
}
