package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.EventManagerRepository
import band.effective.office.tablet.core.domain.repository.EventRepositoryMediator
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import band.effective.office.tablet.core.domain.unbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Implementation of [EventManagerRepository] that manages events between network and local repositories.
 * Handles synchronization, caching, and error recovery for booking operations.
 *
 * @property networkEventRepository Repository for network operations
 * @property localEventStoreRepository Repository for local storage operations
 * @property mediator Mediator for coordinating operations between repositories
 */
class EventManagerRepositoryImpl(
    private val networkEventRepository: BookingRepository,
    private val localEventStoreRepository: LocalBookingRepository,
    private val mediator: EventRepositoryMediator = DefaultEventRepositoryMediator(
        networkRepository = networkEventRepository,
        localRepository = localEventStoreRepository
    )
) : EventManagerRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val updateJob: Job

    init {
        updateJob = scope.launch {
            networkEventRepository.subscribeOnUpdates().collect {
                refreshData()
            }
        }
    }

    /**
     * Cancels all coroutines launched in this scope.
     * Should be called when the EventManager is no longer needed to prevent memory leaks.
     */
    fun dispose() {
        updateJob.cancel()
        scope.cancel()
    }

    /**
     * Returns a flow of room information updates from the local repository.
     * @return Flow of Either containing room information or error with saved data
     */
    override fun getEventsFlow() = localEventStoreRepository.subscribeOnUpdates()

    /**
     * Refreshes room information from the network repository and updates the local repository.
     * If the network operation fails, it will use the saved data from the local repository.
     * @return Either containing the updated room information or error with saved data
     */
    override suspend fun refreshData(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        return mediator.synchronizeData()
    }

    /**
     * Creates a new booking in the specified room.
     * Updates the local repository immediately with a loading state,
     * then attempts to create the booking in the network repository.
     * If the network operation fails, the booking is removed from the local repository.
     * 
     * @param roomName Name of the room to book
     * @param eventInfo Information about the event to create
     * @return Either containing the created event information or an error
     */
    override suspend fun createBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        val roomInfo = getRoomByName(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))

        return mediator.handleBookingCreation(eventInfo, roomInfo)
    }

    /**
     * Updates an existing booking in the specified room.
     * Updates the local repository immediately with a loading state,
     * then attempts to update the booking in the network repository.
     * If the network operation fails, the original event is restored in the local repository.
     * 
     * @param roomName Name of the room where the booking exists
     * @param eventInfo Updated information about the event
     * @return Either containing the updated event information or an error
     */
    override suspend fun updateBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        val roomInfo = getRoomByName(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))

        return mediator.handleBookingUpdate(eventInfo, roomInfo)
    }

    /**
     * Deletes an existing booking in the specified room.
     * Updates the local repository immediately with a loading state,
     * then attempts to delete the booking in the network repository.
     * If the network operation fails, the original event is restored in the local repository.
     * 
     * @param roomName Name of the room where the booking exists
     * @param eventInfo Information about the event to delete
     * @return Either containing a success message or an error
     */
    override suspend fun deleteBooking(roomName: String, eventInfo: EventInfo): Either<ErrorResponse, String> {
        val roomInfo = getRoomByName(roomName)
            ?: return Either.Error(ErrorResponse(404, "Couldn't find a room with name $roomName"))

        return mediator.handleBookingDeletion(eventInfo, roomInfo)
    }

    /**
     * Gets information about all rooms.
     * First tries to get the information from the local repository.
     * If the local repository has no data, it refreshes the data from the network repository.
     * 
     * @return Either containing room information or an error with saved data
     */
    override suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val roomInfos = localEventStoreRepository.getRoomsInfo()
        if (roomInfos as? Either.Error != null
            && roomInfos.error.saveData.isNullOrEmpty()
        ) {
            return refreshData()
        }
        return roomInfos
    }

    /**
     * Gets the current information about all rooms from the local repository without refreshing from the network.
     * This is useful when you need the most recent locally cached data without network latency.
     * 
     * @return Either containing room information or an error with saved data
     */
    override suspend fun getCurrentRoomInfos(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        return localEventStoreRepository.getRoomsInfo()
    }

    /**
     * Gets the names of all available rooms.
     * If no rooms are available, returns a list with the default room name.
     * 
     * @return List of room names
     */
    override suspend fun getRoomNames(): List<String> {
        val rooms = getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        return rooms?.map { it.name } ?: listOf(RoomInfo.defaultValue.name)
    }

    /**
     * Gets information about a specific room by its name.
     * 
     * @param roomName Name of the room to get information about
     * @return Room information or null if the room is not found
     */
    override suspend fun getRoomByName(roomName: String): RoomInfo? {
        val rooms = localEventStoreRepository.getRoomsInfo().unbox(
            errorHandler = { it.saveData }
        )
        val room = rooms?.firstOrNull { it.name == roomName }
        return room
    }
}
