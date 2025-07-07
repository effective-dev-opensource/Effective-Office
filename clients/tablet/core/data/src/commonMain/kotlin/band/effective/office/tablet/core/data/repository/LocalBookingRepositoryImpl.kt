package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.data.utils.Buffer
import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import band.effective.office.tablet.core.domain.unbox
import kotlinx.coroutines.flow.update

/**
 * Implementation of LocalBookingRepository that delegates to LocalEventStoreRepository.
 * This adapter allows the existing implementation to work with the new interfaces.
 */
class LocalBookingRepositoryImpl(
    private val buffer: Buffer,
) : LocalBookingRepository {
    override suspend fun createBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> {
        updateRoomInBuffer(room.name) { events -> events + eventInfo }
        return Either.Success(eventInfo)
    }

    override suspend fun updateBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> {
        updateRoomInBuffer(room.name) { events ->
            val oldEvent = events.firstOrNull {
                it.id == eventInfo.id ||
                        (it.startTime == eventInfo.startTime && it.finishTime == eventInfo.finishTime)
            } ?: return@updateRoomInBuffer events
            events - oldEvent + eventInfo
        }
        return Either.Success(eventInfo)
    }

    override suspend fun deleteBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, String> {
        updateRoomInBuffer(room.name) { events -> events - eventInfo }
        return Either.Success("ok")
    }

    override suspend fun getBooking(eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        return buffer.state.value.unbox(
            errorHandler = { it.saveData }
        )?.firstNotNullOfOrNull {
            it.eventList.firstOrNull { event -> event.id == eventInfo.id }
        }?.let { Either.Success(it) }
            ?: Either.Error(ErrorResponse(404, "Couldn't find booking with id ${eventInfo.id}"))
    }

    private fun updateRoomInBuffer(roomName: String, action: (List<EventInfo>) -> List<EventInfo>) {
        buffer.state.update { either ->
            either.map(
                errorMapper = {
                    val updatedRooms = it.saveData?.updateRoom(roomName, action)
                    it.copy(saveData = updatedRooms)
                },
                successMapper = { it.updateRoom(roomName, action) }
            )
        }
    }

    private fun List<RoomInfo>.updateRoom(
        roomName: String,
        action: (List<EventInfo>) -> List<EventInfo>
    ): List<RoomInfo> {
        val roomIndex = indexOfFirst { it.name == roomName }
        if (roomIndex == -1) return this
        val mutableRooms = toMutableList()
        val room = mutableRooms[roomIndex]
        val newEvents = action(room.eventList)
        mutableRooms[roomIndex] = room.copy(eventList = newEvents)
        return mutableRooms
    }
}