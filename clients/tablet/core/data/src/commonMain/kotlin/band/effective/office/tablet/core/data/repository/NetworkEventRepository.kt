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

    /**
     * Gets information about all rooms with their bookings.
     * Rounds the current time down to the nearest 15-minute interval for the start time,
     * and sets the end time to 14 days from now.
     *
     * @return Either containing room information or an error with saved data
     */
    override suspend fun getRoomsInfo(): Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>> {
        // Get current time
        val now = clock.now()
        val nowLocalDateTime = now.toLocalDateTime(timeZone)

        // Round down to nearest 15-minute interval
        val minutes = nowLocalDateTime.minute
        val roundedMinutes = (minutes / 15) * 15

        // Create rounded start time
        val roundedStart = LocalDateTime(
            year = nowLocalDateTime.year,
            month = nowLocalDateTime.month, // Month is 1-based in constructor
            dayOfMonth = nowLocalDateTime.dayOfMonth,
            hour = nowLocalDateTime.hour,
            minute = roundedMinutes,
            second = 0,
            nanosecond = 0
        )

        // Set end time to 14 days from now
        val finish = now.plus(14, DateTimeUnit.DAY, timeZone)

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
            .map { response ->
                when (response) {
                    is Either.Error -> Either.Error(ErrorWithData(response.error, null))
                    is Either.Success -> {
                        // When we receive booking updates, fetch the latest room information
                        // This is a workaround since we can't directly convert BookingResponseDTO to RoomInfo
                        val roomsInfo = runCatching { getRoomsInfo() }.getOrNull()
                        roomsInfo ?: Either.Success(emptyList())
                    }
                }
            }

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
