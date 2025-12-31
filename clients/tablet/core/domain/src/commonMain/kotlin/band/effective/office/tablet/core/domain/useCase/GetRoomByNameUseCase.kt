package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalRoomRepository
import band.effective.office.shared.core.domain.unbox

/**
 * Use case for getting information about a specific room by its name.
 *
 * @property localRoomRepository Repository for local room storage operations
 */
class GetRoomByNameUseCase(
    private val localRoomRepository: LocalRoomRepository,
) {
    /**
     * Gets information about a specific room by its name.
     *
     * @param roomName Name of the room to get information about
     * @return Room information or null if the room is not found
     */
    suspend operator fun invoke(roomName: String): RoomInfo? {
        val rooms = localRoomRepository.getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        val room = rooms?.firstOrNull { it.name == roomName }
        return room
    }
}