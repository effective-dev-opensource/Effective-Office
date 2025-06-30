package band.effective.office.tablet.core.domain.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.coroutines.flow.Flow

interface EventManagerRepository {
    fun getEventsFlow(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>>
    suspend fun refreshData(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>
    suspend fun createBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, EventInfo>
    suspend fun updateBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, EventInfo>
    suspend fun deleteBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, String>
    suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>
    suspend fun getCurrentRoomInfos(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>
    suspend fun getRoomNames(): List<String>
    suspend fun getRoomByName(roomName: String): RoomInfo?
}