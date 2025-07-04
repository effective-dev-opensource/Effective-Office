package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import band.effective.office.tablet.core.domain.unbox
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class LocalEventStoreRepository(
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) : LocalBookingRepository {
    private val clock: Clock = Clock.System

    private val buffer = MutableStateFlow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>>(
        Either.Error(
            ErrorWithData(
                error = ErrorResponse.getResponse(400),
                saveData = emptyList()
            )
        )
    )

    val flow = buffer.asStateFlow()

    override fun subscribeOnUpdates(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>> = flow

    override fun updateRoomsInfo(either: Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>) {
        buffer.update { either }
    }

    override suspend fun createBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> {
        updateRoomInBuffer(room.name) { events -> events + eventInfo }
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
        return buffer.value.unbox(
            errorHandler = { it.saveData }
        )?.firstNotNullOfOrNull {
            it.eventList.firstOrNull { event -> event.id == eventInfo.id }
        }?.let { Either.Success(it) }
            ?: Either.Error(ErrorResponse(404, "Couldn't find booking with id ${eventInfo.id}"))
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

    private fun updateRoomInBuffer(roomName: String, action: (List<EventInfo>) -> List<EventInfo>) {
        buffer.update { either ->
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

    override suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        return buffer.value.map(
            errorMapper = {
                it.copy(saveData = it.saveData?.map { room -> room.updateCurrentEvent() })
            },
            successMapper = { it.map { room -> room.updateCurrentEvent() } }
        )
    }

    private fun RoomInfo.removePastEvents(): RoomInfo {
        val nowInstant = clock.now()
        val filtered = eventList.filter {
            it.finishTime.toInstant(timeZone) > nowInstant
        }
        return copy(eventList = filtered)
    }

    private fun RoomInfo.updateCurrentEvent(): RoomInfo {
        val now = clock.now().removeSeconds()
        val current = eventList.firstOrNull {
            val start = it.startTime.toInstant(timeZone)
            val end = it.finishTime.toInstant(timeZone)
            start <= now && end > now && !it.isLoading
        } ?: return this

        return copy(
            eventList = eventList - current,
            currentEvent = current
        )
    }

    private fun Instant.removeSeconds(): Instant {
        val local = this.toLocalDateTime(timeZone)
        val rounded = LocalDateTime(
            year = local.year,
            month = local.month,
            dayOfMonth = local.dayOfMonth,
            hour = local.hour,
            minute = local.minute,
            second = 0,
            nanosecond = 0
        )
        return rounded.toInstant(timeZone)
    }
}