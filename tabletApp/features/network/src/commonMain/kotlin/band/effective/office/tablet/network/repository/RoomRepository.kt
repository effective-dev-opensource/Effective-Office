package band.effective.office.tablet.network.repository

import band.effective.office.network.model.Either
import band.effective.office.tablet.domain.model.ErrorWithData
import band.effective.office.tablet.domain.model.RoomInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
/**Repository for get information about room*/
interface RoomRepository {
    /**Get info about current rom
     * @param room room name*/
    suspend fun getRoomInfo(room: String): Either<ErrorWithData<RoomInfo>, RoomInfo>
    /**Get list all rooms*/
    suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>
    fun subscribeOnUpdates(
        roomId: String,
        scope: CoroutineScope
    ): Flow<Either<ErrorWithData<RoomInfo>, RoomInfo>>
}