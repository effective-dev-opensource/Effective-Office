package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.data.api.BookingApi
import band.effective.office.tablet.core.data.api.WorkspaceApi
import band.effective.office.tablet.core.data.mapper.RoomInfoMapper
import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.RoomRepository
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.core.domain.util.defaultTimeZone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

/**
 * Implementation of RoomRepository that delegates to NetworkEventRepository.
 * This adapter allows the existing implementation to work with the new interfaces.
 */
class RoomRepositoryImpl(
    private val api: BookingApi,
    private val workspaceApi: WorkspaceApi,
    private val roomInfoMapper: RoomInfoMapper,
) : RoomRepository {

    override fun subscribeOnUpdates(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>> {
        return api.subscribeOnBookingsList("")
            .map { response ->
                when (response) {
                    is Either.Error -> Either.Error(ErrorWithData(response.error, null))
                    is Either.Success -> Either.Success(emptyList())
                }
            }
    }

    override suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        // Get current time
        val now = currentLocalDateTime
        // Round down to nearest 15-minute interval
        val minutes = now.minute
        val roundedMinutes = (minutes / 15) * 15

        // Create rounded start time
        val roundedStart = LocalDateTime(
            year = now.year,
            month = now.month,
            dayOfMonth = now.dayOfMonth,
            hour = now.hour,
            minute = roundedMinutes,
            second = 0,
            nanosecond = 0
        )

        // Set end time to 14 days from now
        val finish = now.asInstant.plus(14, DateTimeUnit.DAY, defaultTimeZone)

        val response = workspaceApi.getWorkspacesWithBookings(
            tag = "meeting",
            freeFrom = roundedStart.asInstant.toEpochMilliseconds(),
            freeUntil = finish.toEpochMilliseconds()
        )

        return when (response) {
            is Either.Error -> Either.Error(ErrorWithData(response.error, null))
            is Either.Success -> Either.Success(response.data.map(roomInfoMapper::map))
        }
    }
}