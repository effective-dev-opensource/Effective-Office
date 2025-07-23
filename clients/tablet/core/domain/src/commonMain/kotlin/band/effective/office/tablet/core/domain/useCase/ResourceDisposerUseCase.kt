package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.repository.RoomRepository
    import band.effective.office.tablet.core.domain.util.Loggable
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
) : Loggable {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override val loggableCoroutineScope: CoroutineScope = scope
    private var updateJob: Job? = null

    operator fun invoke() {
        logAsyncOperation(
            operationName = "subscribeToNetworkUpdates"
        ) {
            updateJob = scope.launch {
                networkRoomRepository.subscribeOnUpdates().debounce(2000).collectLatest {

                    logSuspendOperation(
                        operationName = "refreshOnUpdate",
                        resultMessage = { _ -> "triggered data refresh" }
                    ) {
                        refreshDataUseCase()
                    }
                }
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