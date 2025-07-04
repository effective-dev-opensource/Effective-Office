package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.data.api.BookingApi
import band.effective.office.tablet.core.data.api.WorkspaceApi
import band.effective.office.tablet.core.data.dto.booking.BookingRequestDTO
import band.effective.office.tablet.core.data.dto.booking.BookingResponseDTO
import band.effective.office.tablet.core.data.dto.workspace.WorkspaceDTO
import band.effective.office.tablet.core.data.utils.Converter.toOrganizer
import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.repository.BookingRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class NetworkEventRepository(
    private val api: BookingApi,
    private val workspaceApi: WorkspaceApi,
) : BookingRepository {
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
    private val clock: Clock = Clock.System

    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        val now = clock.now().toLocalDateTime(timeZone)
        val minutes = now.minute
        val excess = minutes % 15 + 1
        val adjustedNow = now
            .toInstant(timeZone)
            .minus(excess, DateTimeUnit.MINUTE)
            .toLocalDateTime(timeZone)
        val roundedStart = LocalDateTime(
            year = adjustedNow.year,
            hour = adjustedNow.hour,
            minute = adjustedNow.minute,
            second = 0,
            nanosecond = 0,
            monthNumber = adjustedNow.month.ordinal,
            dayOfMonth = adjustedNow.dayOfMonth
        )

        val finish = now.toInstant(timeZone).plus(14, DateTimeUnit.DAY, timeZone)

        val response = workspaceApi.getWorkspacesWithBookings(
            tag = "meeting",
            freeFrom = roundedStart.toInstant(timeZone).toEpochMilliseconds(),
            freeUntil = finish.toEpochMilliseconds()
        )

        return when (response) {
            is Either.Error -> Either.Error(ErrorWithData(response.error, null))
            is Either.Success -> Either.Success(response.data.map { it.toRoom() })
        }
    }

    override suspend fun createBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> =
        api.createBooking(eventInfo.toBookingRequestDTO(room))
            .map(errorMapper = { it }, successMapper = { it.toEventInfo() })

    override suspend fun updateBooking(
        eventInfo: EventInfo,
        room: RoomInfo
    ): Either<ErrorResponse, EventInfo> =
        api.updateBooking(eventInfo.toBookingRequestDTO(room), eventInfo.id)
            .map(errorMapper = { it }, successMapper = { it.toEventInfo() })

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
            successMapper = { it.toEventInfo() }
        )
    }

    override fun subscribeOnUpdates(): Flow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>> =
        api.subscribeOnBookingsList("", scope)
            .map { Either.Success(emptyList()) }

    /** Map domain model to DTO */
    private fun EventInfo.toBookingRequestDTO(room: RoomInfo): BookingRequestDTO = BookingRequestDTO(
        beginBooking = this.startTime.toInstant(timeZone).toEpochMilliseconds(),
        endBooking = this.finishTime.toInstant(timeZone).toEpochMilliseconds(),
        ownerEmail = this.organizer.email,
        participantEmails = listOfNotNull(this.organizer.email),
        workspaceId = room.id
    )

    /** Map DTO to domain model */
    private fun BookingResponseDTO.toEventInfo(): EventInfo = EventInfo(
        id = id,
        startTime = Instant.fromEpochMilliseconds(beginBooking).toLocalDateTime(timeZone),
        finishTime = Instant.fromEpochMilliseconds(endBooking).toLocalDateTime(timeZone),
        organizer = owner?.toOrganizer() ?: Organizer.default,
        isLoading = false,
    )

    private fun WorkspaceDTO.toRoom(): RoomInfo =
        RoomInfo(
            name = name,
            capacity = utilities.firstOrNull { it.name == "place" }?.count ?: 0,
            isHaveTv = utilities.any { it.name == "tv" },
            socketCount = utilities.firstOrNull { it.name == "lan" }?.count ?: 0,
            eventList = bookings?.map { it.toEventInfo() } ?: emptyList(),
            currentEvent = null,
            id = id
        )
}