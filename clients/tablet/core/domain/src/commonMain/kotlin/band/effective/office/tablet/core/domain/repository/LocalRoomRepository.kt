package band.effective.office.tablet.core.domain.repository

import band.effective.office.shared.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for local room-related operations.
 * Extends [RoomRepository] to provide additional methods for local storage operations.
 */
interface LocalRoomRepository {
    /**
     * Updates the local storage with room information.
     * This method is typically called after fetching data from a remote source.
     *
     * @param either Either containing room information or an error with saved data
     */
    fun updateRoomsInfo(either: Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>)

    /**
     * Subscribes to updates about rooms and their bookings.
     *
     * @return Flow of Either containing room information or an error with saved data
     */
    fun subscribeOnUpdates(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>>

    /**
     * Gets information about all rooms with their bookings.
     *
     * @return Either containing room information or an error with saved data
     */
    suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>
}