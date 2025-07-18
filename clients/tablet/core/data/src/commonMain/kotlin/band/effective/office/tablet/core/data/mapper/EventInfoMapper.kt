package band.effective.office.tablet.core.data.mapper

import band.effective.office.tablet.core.data.dto.booking.BookingRequestDTO
import band.effective.office.tablet.core.data.dto.booking.BookingResponseDTO
import band.effective.office.tablet.core.data.utils.Converter.toOrganizer
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import kotlinx.datetime.Instant

class EventInfoMapper {

    fun map(dto: BookingResponseDTO): EventInfo =
        EventInfo(
            id = dto.id,
            startTime = Instant.fromEpochMilliseconds(dto.beginBooking).asLocalDateTime,
            finishTime = Instant.fromEpochMilliseconds(dto.endBooking).asLocalDateTime,
            organizer = dto.owner?.toOrganizer() ?: Organizer.default,
            isLoading = false,
            isEditable = dto.isEditable,
        )

    fun mapToRequest(eventInfo: EventInfo, roomInfo: RoomInfo): BookingRequestDTO = BookingRequestDTO(
        beginBooking = eventInfo.startTime.asInstant.toEpochMilliseconds(),
        endBooking = eventInfo.finishTime.asInstant.toEpochMilliseconds(),
        ownerEmail = eventInfo.organizer.email,
        participantEmails = listOfNotNull(eventInfo.organizer.email),
        workspaceId = roomInfo.id
    )
}
