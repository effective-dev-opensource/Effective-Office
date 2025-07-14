package band.effective.office.tablet.core.domain.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for room-related operations.
 * Provides methods for retrieving information about rooms and their bookings.
 */
interface RoomRepository {
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