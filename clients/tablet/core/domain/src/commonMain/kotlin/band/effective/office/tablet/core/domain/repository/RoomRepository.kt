package band.effective.office.tablet.core.domain.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**Repository for get information about room*/
interface RoomRepository {
    /**Get list all rooms*/
    suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>

    fun subscribeOnUpdates(
        scope: CoroutineScope
    ): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>>

    suspend fun updateCashe()
}