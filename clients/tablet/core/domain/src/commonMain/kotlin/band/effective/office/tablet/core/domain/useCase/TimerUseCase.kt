package band.effective.office.tablet.core.domain.useCase

import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**Custom timers*/
class TimerUseCase {
    fun timerFlow(delay: Duration) = flow {
        var i = 0L
        while (true) {
            delay(delay)
            emit(value = i++)
        }
    }

    fun timer(
        scope: CoroutineScope,
        delay: Duration,
        onTick: suspend CoroutineScope.(Long) -> Unit
    ) =
        scope.launch(Dispatchers.IO) {
            timerFlow(delay).collect { onTick(it) }
        }
}