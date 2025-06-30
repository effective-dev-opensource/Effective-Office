package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import band.effective.office.tablet.core.domain.unbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class EventManager(
    private val networkEventRepository: BookingRepository,
    private val localEventStoreRepository: LocalBookingRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            networkEventRepository.subscribeOnUpdates().collect {
                refreshData()
            }
        }
    }

    fun getEventsFlow() = localEventStoreRepository.subscribeOnUpdates()

    suspend fun refreshData(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val save = localEventStoreRepository.getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        val roomInfos = networkEventRepository.getRoomsInfo()
            .map(
                errorMapper = { it.copy(saveData = save) },
                successMapper = { it }
            )
        localEventStoreRepository.updateRoomsInfo(roomInfos)
        return roomInfos
    }

    suspend fun createBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        val loadingEvent = eventInfo.copy(isLoading = true)
        val roomInfo = getRoomByName(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))

        localEventStoreRepository.createBooking(loadingEvent, roomInfo)
        val response = networkEventRepository.createBooking(loadingEvent, roomInfo)
        when (response) {
            is Either.Error -> {
                localEventStoreRepository.deleteBooking(loadingEvent, roomInfo)
            }

            is Either.Success -> {
                val event = response.data
                localEventStoreRepository.updateBooking(event, roomInfo)
            }
        }
        return response
    }

    suspend fun updateBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        val loadingEvent = eventInfo.copy(isLoading = true)
        val oldEvent = localEventStoreRepository.getBooking(eventInfo) as? Either.Success
            ?: return Either.Error(ErrorResponse(404, "Old event with id ${eventInfo.id} wasn't found"))
        val roomInfo = getRoomByName(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))

        localEventStoreRepository.updateBooking(loadingEvent, roomInfo)
        val response = networkEventRepository.updateBooking(loadingEvent, roomInfo)
        when (response) {
            is Either.Error -> {
                localEventStoreRepository.updateBooking(oldEvent.data, roomInfo)
            }

            is Either.Success -> {
                val event = response.data
                localEventStoreRepository.updateBooking(event, roomInfo)
            }
        }
        return response
    }

    suspend fun deleteBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, String> {
        val loadingEvent = eventInfo.copy(isLoading = true)
        val roomInfo = getRoomByName(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))
        localEventStoreRepository.updateBooking(loadingEvent, roomInfo)
        val response = networkEventRepository.deleteBooking(loadingEvent, roomInfo)
        when (response) {
            is Either.Error -> {
                localEventStoreRepository.createBooking(eventInfo, roomInfo)
            }

            is Either.Success -> {
                localEventStoreRepository.deleteBooking(loadingEvent, roomInfo)
            }
        }
        return response
    }

    suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val roomInfos = localEventStoreRepository.getRoomsInfo()
        if (roomInfos as? Either.Error != null
            && roomInfos.error.saveData.isNullOrEmpty()
        ) {
            return refreshData()
        }
        return roomInfos
    }

    suspend fun getCurrentRoomInfos(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        return localEventStoreRepository.getRoomsInfo()
    }

    suspend fun getRoomNames(): List<String> {
        val rooms = getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        return rooms?.map { it.name } ?: listOf(RoomInfo.defaultValue.name)
    }

    suspend fun getRoomByName(roomName: String): RoomInfo? {
        val rooms = localEventStoreRepository.getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        val room = rooms?.firstOrNull { it.name == roomName }
        return room
    }
}