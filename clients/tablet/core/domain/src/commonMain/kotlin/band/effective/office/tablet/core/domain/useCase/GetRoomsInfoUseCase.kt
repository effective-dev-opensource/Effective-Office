package band.effective.office.tablet.core.domain.useCase

import band.effective.office.shared.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalRoomRepository

/**
 * Use case for getting information about all rooms.
 *
 * @property localRoomRepository Repository for local room storage operations
 * @property refreshDataUseCase Use case for refreshing room information
 */
class GetRoomsInfoUseCase(
    private val localRoomRepository: LocalRoomRepository,
    private val refreshDataUseCase: RefreshDataUseCase,
) {
    /**
     * Gets information about all rooms, from the local repository while it holds data and from the
     * network whenever it holds a failure — a cached failure is not data, however much of the
     * previous answer it carries along.
     */
    suspend operator fun invoke(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val roomInfos = localRoomRepository.getRoomsInfo()
        if (roomInfos is Either.Error) {
            return refreshDataUseCase()
        }
        return roomInfos
    }
}