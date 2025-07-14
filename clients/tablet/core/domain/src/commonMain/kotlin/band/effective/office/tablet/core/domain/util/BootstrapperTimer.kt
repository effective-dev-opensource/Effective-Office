package band.effective.office.tablet.core.domain.util

import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BootstrapperTimer(
    private val timerUseCase: TimerUseCase,
    private val coroutineScope: CoroutineScope,
) {

    private var job: Job? = null
    private var tickHandler: suspend (Long) -> Unit = {}
    private var timerDelay: Duration? = null

    fun init(
        delay: Duration,
        handler: suspend (Long) -> Unit,
    ) {
        tickHandler = handler
        timerDelay = delay
    }

    fun start(
        delay: Duration,
        handler: suspend (Long) -> Unit,
    ) {
        init(delay, handler)
        restart()
    }

    fun restart(delay: Duration = timerDelay ?: 1.seconds) {
        job?.cancel()
        job = coroutineScope.launch(Dispatchers.IO) {
            timerUseCase.timerFlow(delay).collect {
                with(coroutineScope) { tickHandler(it) }
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}