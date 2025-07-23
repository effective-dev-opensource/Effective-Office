package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.unbox
import band.effective.office.tablet.core.domain.util.Loggable
import kotlinx.coroutines.CoroutineScope

/**
 * Use case for getting the names of all available rooms.
 *
 * @property getRoomsInfoUseCase Use case for getting information about all rooms
 */
class GetRoomNamesUseCase(
    private val getRoomsInfoUseCase: GetRoomsInfoUseCase,
) : Loggable {
    override val loggableCoroutineScope: CoroutineScope? = null
    /**
     * Gets the names of all available rooms.
     * If no rooms are available, returns a list with the default room name.
     *
     * @return List of room names
     */
    suspend operator fun invoke(): List<String> =
        logSuspendOperation(
            operationName = "getRoomNames",
            resultMessage = { result -> if (result.size == 1 && result[0] == RoomInfo.defaultValue.name) "default=${result[0]}" else "${result.size} rooms" }
        ) {
        val rooms = getRoomsInfoUseCase().unbox(
            errorHandler = { it.saveData }
        )
        rooms?.map { it.name } ?: listOf(RoomInfo.defaultValue.name)
    }
}
