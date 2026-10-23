package band.effective.office.tablet.core.ui.inactivity

import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A restartable countdown: fires [onTimeout] once [timeout] has passed with no call to [restart].
 * `collectLatest` cancels the pending delay on every restart, so no platform handler is needed.
 */
class InactivityTimer(
    private val timeout: Duration,
    private val onTimeout: () -> Unit,
) {
    private val restarts = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** Starts the countdown; cancelling the returned job stops it. */
    fun start(scope: CoroutineScope): Job {
        restart()
        return scope.launch {
            restarts.collectLatest {
                delay(timeout)
                onTimeout()
            }
        }
    }

    /** Call on any user interaction: puts the full [timeout] back on the clock. */
    fun restart() {
        restarts.tryEmit(Unit)
    }
}
