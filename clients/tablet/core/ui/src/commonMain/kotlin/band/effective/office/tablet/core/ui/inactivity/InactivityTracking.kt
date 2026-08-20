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
 * Inactivity countdown, fed by [InactivityTracker] through [onUserInteraction]. The countdown
 * belongs to the caller of [start]: only the token it returned can [stop] it.
 */
class InactivityTracking {

    class Owner internal constructor()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _timeouts = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Fires once the tablet has sat untouched for the timeout. A flow rather than a callback
     * because two layers react to the same tick: the date resets and the open modal closes.
     */
    val timeouts: SharedFlow<Unit> = _timeouts.asSharedFlow()

    private var timer: InactivityTimer? = null
    private var job: Job? = null
    private var owner: Owner? = null

    /** Idempotent: a second call replaces the running countdown instead of stacking another one. */
    fun start(timeout: Duration = DEFAULT_TIMEOUT): Owner {
        cancel()
        val countdown = InactivityTimer(timeout) { _timeouts.tryEmit(Unit) }
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
