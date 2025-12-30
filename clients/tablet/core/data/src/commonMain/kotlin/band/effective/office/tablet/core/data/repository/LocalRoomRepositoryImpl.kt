package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.data.utils.Buffer
import band.effective.office.shared.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.shared.core.domain.map
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.LocalRoomRepository
import band.effective.office.shared.core.utils.asInstant
import band.effective.office.shared.core.utils.cropSeconds
import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update

/**
 * Implementation of LocalRoomRepository that delegates to LocalEventStoreRepository.
 * This adapter allows the existing implementation to work with the new interfaces.
 */
class LocalRoomRepositoryImpl(
    private val buffer: Buffer,
) : LocalRoomRepository {

    override fun subscribeOnUpdates(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>> = buffer.state

    override fun updateRoomsInfo(either: Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>) {
        buffer.state.update { either }
    }

    override suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        return buffer.state.value.map(
            errorMapper = {
                it.copy(saveData = it.saveData?.map { room -> room.updateCurrentEvent() })
            },
            successMapper = { it.map { room -> room.updateCurrentEvent() } }
        )
    }

    private fun RoomInfo.updateCurrentEvent(): RoomInfo {
        val now = currentLocalDateTime.cropSeconds().asInstant
        val current = eventList.firstOrNull {
            val start = it.startTime.asInstant
            val end = it.finishTime.asInstant
            start <= now && end > now && !it.isLoading
        } ?: return this

        return copy(
            eventList = eventList - current,
            currentEvent = current
        )
    }
}