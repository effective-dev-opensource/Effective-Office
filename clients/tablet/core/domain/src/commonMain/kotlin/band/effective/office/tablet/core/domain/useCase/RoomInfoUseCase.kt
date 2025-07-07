package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.unbox
import kotlin.collections.filter
import kotlinx.datetime.Clock
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/** Use case for getting info about rooms */
class RoomInfoUseCase(
    private val getRoomNamesUseCase: GetRoomNamesUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getRoomsInfoUseCase: GetRoomsInfoUseCase,
    private val getEventsFlowUseCase: GetEventsFlowUseCase,
    private val getRoomByNameUseCase: GetRoomByNameUseCase,
    private val getCurrentRoomInfosUseCase: GetCurrentRoomInfosUseCase,
) {
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
    private val clock: Clock = Clock.System

    /** Get all room names */
    suspend fun getRoomsNames(): List<String> {
        return getRoomNamesUseCase()
    }

    /** Update repository cache */
    suspend fun updateCache() = refreshDataUseCase()

    /** Get info about all rooms (filtered by future events) */
    suspend operator fun invoke() = getRoomsInfoUseCase()
        .map(
            errorMapper = { it },
            successMapper = {
                val now = clock.now()
                it.map { room ->
                    room.copy(eventList = room.eventList.filter { event ->
                        event.startTime.toInstant(timeZone) > now
                    })
                }
            }
        )

    /** Get updated room flow (filtered by future events) */
    fun subscribe() =
        getEventsFlowUseCase().map { either ->
            either.unbox(
                errorHandler = { it.saveData }
            ) ?: emptyList()
        }

    /** Get info about room by name */
    suspend fun getRoom(room: String) = getRoomByNameUseCase(room)

    /** Get cached info about current rooms */
    suspend fun getCurrentRooms() = getCurrentRoomInfosUseCase()

    private fun List<RoomInfo>.mapRoomsInfo(): List<RoomInfo> {
        val now = clock.now()
        return map { room ->
            room.copy(eventList = room.eventList.filter { event ->
                event.startTime.toInstant(timeZone) > now
            })
        }
    }
}
