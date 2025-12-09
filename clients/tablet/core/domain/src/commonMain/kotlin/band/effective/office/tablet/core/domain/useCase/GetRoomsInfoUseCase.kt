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
     * Gets information about all rooms.
     * First tries to get the information from the local repository.
     * If the local repository has no data, it refreshes the data from the network repository.
     *
     * @return Either containing room information or an error with saved data
     */
    suspend operator fun invoke(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val roomInfos = localRoomRepository.getRoomsInfo()
        if (roomInfos as? Either.Error != null
            && roomInfos.error.saveData.isNullOrEmpty()
        ) {
            return refreshDataUseCase()
        }
        return roomInfos
    }
}