package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalRoomRepository
import band.effective.office.tablet.core.domain.util.Loggable
import kotlinx.coroutines.CoroutineScope

/**
 * Use case for getting the current information about all rooms from the local repository.
 *
 * @property localRoomRepository Repository for local room storage operations
 */
class GetCurrentRoomInfosUseCase(
    private val localRoomRepository: LocalRoomRepository,
) : Loggable {
    override val loggableCoroutineScope: CoroutineScope? = null
    /**
     * Gets the current information about all rooms from the local repository without refreshing from the network.
     * This is useful when you need the most recent locally cached data without network latency.
     *
     * @return Either containing room information or an error with saved data
     */
    suspend operator fun invoke(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> =
        logSuspendOperation(
            operationName = "getCurrentRoomInfos"
        ) {
            localRoomRepository.getRoomsInfo()
    }
}