package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.repository.RoomRepository
import io.github.aakira.napier.Napier
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
 */
class ResourceDisposerUseCase(
    private val networkRoomRepository: RoomRepository,
    private val refreshDataUseCase: RefreshDataUseCase,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var updateJob: Job? = null

    operator fun invoke() {
        Napier.d { "[ResourceDisposerUseCase] Starting subscription to network updates" }
        updateJob = scope.launch {
            networkRoomRepository.subscribeOnUpdates().debounce(2000).collectLatest {
                Napier.d { "[ResourceDisposerUseCase] Received update, triggering data refresh" }
                refreshDataUseCase()
            }
        }
    }

    /**
     * Cancels all coroutines launched in this scope.
     * Should be called when the use case is no longer needed to prevent memory leaks.
     */
    fun dispose() {
        updateJob?.cancel()
        scope.cancel()
    }
}