package band.effective.office.tablet.core.domain.useCase

import band.effective.office.shared.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalRoomRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting a flow of room information updates.
 *
 * @property localRoomRepository Repository for local room storage operations
 */
class GetEventsFlowUseCase(
    private val localRoomRepository: LocalRoomRepository,
) {
    /**
     * Returns a flow of room information updates from the local repository.
     * @return Flow of Either containing room information or error with saved data
     */
    operator fun invoke(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>> {
        return localRoomRepository.subscribeOnUpdates()
    }
}