package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.repository.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * Use case for managing resources and background tasks.
 * Handles subscription to network updates and resource cleanup.
 *
 * @property networkRoomRepository Repository for network room operations
 * @property refreshDataUseCase Use case for refreshing room information
 * @property periodicRoomRefreshUseCase Polling for platforms without push
 */
class ResourceDisposerUseCase(
    private val networkRoomRepository: RoomRepository,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val periodicRoomRefreshUseCase: PeriodicRoomRefreshUseCase,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var updateJob: Job? = null
    private var pollJob: Job? = null

    operator fun invoke() {
        updateJob = scope.launch {
            networkRoomRepository.subscribeOnUpdates().debounce(2000).collectLatest {
                refreshDataUseCase()
            }
        }
        pollJob?.cancel()
        pollJob = periodicRoomRefreshUseCase.start(scope)
    }

    /**
     * Cancels all coroutines launched in this scope.
     * Should be called when the use case is no longer needed to prevent memory leaks.
     */
    fun dispose() {
        updateJob?.cancel()
        pollJob?.cancel()
        scope.cancel()
    }
}