package band.effective.office.tablet.core.domain.useCase

import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Re-reads the rooms from the server on a timer, where push is not available.
 *
 * Not the same thing as [UpdateUseCase], which also ticks but only asks the screen to reload from
 * the local cache. A `null` [interval] leaves the platform on push and starts nothing.
 */
class PeriodicRoomRefreshUseCase(
    private val refreshDataUseCase: RefreshDataUseCase,
    private val interval: Duration?,
) {
    fun start(scope: CoroutineScope): Job? = interval?.let { period ->
        scope.launch {
            while (true) {
                delay(period)
                refreshDataUseCase()
            }
        }
    }
}
