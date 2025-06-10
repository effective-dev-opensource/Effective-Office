package band.effective.office.tablet.network.repository.impl

import band.effective.office.network.api.Api
import band.effective.office.network.dto.BookingRequestDTO
import band.effective.office.network.dto.BookingResponseDTO
import band.effective.office.network.dto.WorkspaceDTO
import band.effective.office.network.model.Either
import band.effective.office.network.model.ErrorResponse
import band.effective.office.tablet.domain.model.ErrorWithData
import band.effective.office.tablet.domain.model.EventInfo
import band.effective.office.tablet.domain.model.Organizer
import band.effective.office.tablet.domain.model.RoomInfo
import band.effective.office.tablet.network.repository.BookingRepository
import band.effective.office.tablet.utils.Converter.toOrganizer
import band.effective.office.tablet.utils.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.GregorianCalendar

class NetworkEventRepository(
    private val api: Api,
) : BookingRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val start = GregorianCalendar().apply {
            val minutes = get(Calendar.MINUTE)
            val excess = minutes % 15 + 1
            add(Calendar.MINUTE, -excess)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val finish = GregorianCalendar().apply { add(Calendar.DAY_OF_MONTH, 14) }
        val response = api.getWorkspacesWithBookings(
            tag = "meeting",
            freeFrom = start.timeInMillis,
            freeUntil = finish.timeInMillis
        )
        return when (response) {
            is Either.Error -> {
                Either.Error(ErrorWithData(response.error, null))
            }

            is Either.Success -> {
                Either.Success(response.data.map { it.toRoom() })
            }
        }
    }

    override suspend fun createBooking(
        eventInfo: EventInfo,
        room: RoomInfo,
    ): Either<ErrorResponse, EventInfo> = api.createBooking(eventInfo.toBookingRequestDTO(room))
        .map(errorMapper = { it }, successMapper = { it.toEventInfo() })

    override suspend fun updateBooking(
        eventInfo: EventInfo,
        room: RoomInfo,
    ): Either<ErrorResponse, EventInfo> =
        api.updateBooking(eventInfo.toBookingRequestDTO(room), eventInfo.id)
            .map(errorMapper = { it }, successMapper = { it.toEventInfo() })

    override suspend fun deleteBooking(
        eventInfo: EventInfo,
        room: RoomInfo,
    ): Either<ErrorResponse, String> =
        api.deleteBooking(eventInfo.id)
            .map(
                errorMapper = { it },
                successMapper = { "ok" },
            )

    override suspend fun getBooking(
        eventInfo: EventInfo
    ): Either<ErrorResponse, EventInfo> {
        val response = api.getBooking(eventInfo.id)
        return response.map(
            errorMapper = { it },
            successMapper = { it.toEventInfo() }
        )
    }

    override fun subscribeOnUpdates(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>> =
        api.subscribeOnBookingsList("", scope)
            .map { Either.Success(emptyList()) }

    /**Map domain model to DTO*/
    private fun EventInfo.toBookingRequestDTO(room: RoomInfo): BookingRequestDTO =
        BookingRequestDTO(
            beginBooking = this.startTime.timeInMillis,
            endBooking = this.finishTime.timeInMillis,
            ownerEmail = this.organizer.email,
            participantEmails = listOfNotNull(this.organizer.email),
            workspaceId = room.id
        )

    /**Map DTO to domain model*/
    private fun BookingResponseDTO.toEventInfo(): EventInfo = EventInfo(
        id = id,
        startTime = GregorianCalendar().apply { timeInMillis = beginBooking },
        finishTime = GregorianCalendar().apply { timeInMillis = endBooking },
        organizer = owner?.toOrganizer() ?: Organizer.default,
        isLoading = false,
    )

    private fun WorkspaceDTO.toRoom() =
        RoomInfo(
            name = name,
            capacity = utilities.firstOrNull { it.name == "place" }?.count ?: 0,
            isHaveTv = utilities.firstOrNull { it.name == "tv" } != null,
            socketCount = utilities.firstOrNull { it.name == "lan" }?.count ?: 0,
            eventList = bookings?.map { it.toEventInfo() } ?: emptyList(),
            currentEvent = null,
            id = id
        )
}