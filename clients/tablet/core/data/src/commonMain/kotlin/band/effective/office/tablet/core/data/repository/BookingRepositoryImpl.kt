package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.data.api.BookingApi
import band.effective.office.tablet.core.data.mapper.EventInfoMapper
import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.shared.core.domain.map
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository

/**
 * Implementation of BookingRepository that delegates to NetworkEventRepository.
 * This adapter allows the existing implementation to work with the new interfaces.
 */
class BookingRepositoryImpl(
    private val api: BookingApi,
    private val eventInfoMapper: EventInfoMapper,
) : BookingRepository {

    override suspend fun createBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> =
        api.createBooking(eventInfoMapper.mapToRequest(eventInfo, room))
            .map(errorMapper = { it }, successMapper = eventInfoMapper::map)

    override suspend fun updateBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> =
        api.updateBooking(eventInfoMapper.mapToRequest(eventInfo, room), eventInfo.id)
            .map(errorMapper = { it }, successMapper = eventInfoMapper::map)

    override suspend fun deleteBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, String> = api.deleteBooking(eventInfo.id).map(
        errorMapper = { it },
        successMapper = { "ok" }
    )

    override suspend fun getBooking(eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        val response = api.getBooking(eventInfo.id)
        return response.map(
            errorMapper = { it },
            successMapper = eventInfoMapper::map,
        )
    }
}