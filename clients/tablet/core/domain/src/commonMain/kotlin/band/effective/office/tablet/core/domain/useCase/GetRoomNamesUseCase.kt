package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.shared.core.domain.unbox

/**
 * Use case for getting the names of all available rooms.
 *
 * @property getRoomsInfoUseCase Use case for getting information about all rooms
 */
class GetRoomNamesUseCase(
    private val getRoomsInfoUseCase: GetRoomsInfoUseCase,
) {
    /**
     * Gets the names of all available rooms.
     * If no rooms are available, returns a list with the default room name.
     *
     * @return List of room names
     */
    suspend operator fun invoke(): List<String> {
        val rooms = getRoomsInfoUseCase().unbox(
            errorHandler = { it.saveData }
        )
        return rooms?.map { it.name } ?: listOf(RoomInfo.defaultValue.name)
    }
}