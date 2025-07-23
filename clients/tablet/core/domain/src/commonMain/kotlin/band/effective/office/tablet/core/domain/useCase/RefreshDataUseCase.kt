package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalRoomRepository
import band.effective.office.tablet.core.domain.repository.RoomRepository
import band.effective.office.tablet.core.domain.unbox
import band.effective.office.tablet.core.domain.util.Loggable
import kotlinx.coroutines.CoroutineScope

/**
 * Use case for refreshing room information from the network repository.
 *
 * @property networkRoomRepository Repository for network room operations
 * @property localRoomRepository Repository for local room storage operations
 */
class RefreshDataUseCase(
    private val networkRoomRepository: RoomRepository,
    private val localRoomRepository: LocalRoomRepository,
) : Loggable {
    override val loggableCoroutineScope: CoroutineScope? = null
    /**
     * Refreshes room information from the network repository and updates the local repository.
     * If the network operation fails, it will use the saved data from the local repository.
     * 
     * @return Either containing the updated room information or error with saved data
     */
    suspend operator fun invoke(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> =
        logSuspendOperation(
            operationName = "refreshData"
        ) {
            synchronizeData()
        }

    /**
     * Synchronizes data between network and local repositories.
     * Fetches data from the network and updates the local repository.
     * If the network operation fails, it will use the saved data from the local repository.
     *
     * @return Either containing the synchronized room information or an error with saved data
     */
    private suspend fun synchronizeData(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> =
        logSuspendOperationWithError(
            operationName = "synchronizeData",
        ) {
        val save = localRoomRepository.getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        val roomInfos = networkRoomRepository.getRoomsInfo()
            .map(
                errorMapper = { error ->
                    // Prevent NPE by handling null save data
                    error.copy(saveData = save)
                },
                successMapper = { it }
            )
        localRoomRepository.updateRoomsInfo(roomInfos)
        roomInfos
    }  ?: Either.Error(ErrorWithData(ErrorResponse(code=500, description="Unexpected error"), saveData=emptyList()))
}