package band.effective.office.tablet.core.domain.useCase

import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Re-reads the rooms from the server on a timer, where push is not available.
 *
 * This is not the same thing as [UpdateUseCase], which also ticks: that one only asks the screen
 * to reload, and the reload goes through [GetRoomsInfoUseCase], which serves the local cache and
 * only reaches the network when the cache is empty. Refreshing the cache is [RefreshDataUseCase],
 * and after startup nothing calls it unless a push arrives.
 *
 * Writing to the cache is enough on its own: the screen is already subscribed to it through
 * `RoomInfoUseCase.subscribe()`, so the existing chain carries the update the rest of the way.
 *
 * @param interval `null` turns polling off — see
 * [band.effective.office.tablet.core.domain.platform.roomRefreshInterval].
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
