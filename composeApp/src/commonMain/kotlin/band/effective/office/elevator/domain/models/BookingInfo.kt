package band.effective.office.elevator.domain.models

import androidx.compose.ui.text.intl.Locale
import band.effective.office.elevator.ui.models.ReservedSeat
import band.effective.office.elevator.utils.MonthLocalizations
import band.effective.office.elevator.utils.capitalizeFirstLetter
import band.effective.office.elevator.utils.localDateTimeToUnix
import band.effective.office.elevator.utils.unixToLocalDateTime
import band.effective.office.network.dto.BookingRequestDTO
import band.effective.office.network.dto.BookingResponseDTO
import band.effective.office.network.dto.RecurrenceDTO
import band.effective.office.network.dto.UserDTO
import band.effective.office.network.dto.WorkspaceDTO
import epicarchitect.calendar.compose.basis.localizedBySystem
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

data class BookingInfo(
    val id: String,
    val ownerId: String,
    val workSpaceId: String,
    val seatName: String,
    val dateOfStart: LocalDateTime,
    val dateOfEnd: LocalDateTime
)

fun BookingResponseDTO.toDomainModel() =
    BookingInfo(
        id = id,
        ownerId = owner?.id ?: "",
        workSpaceId = workspace.id,
        seatName = "${workspace.zone?.name.orEmpty()} ${workspace.name}",
        dateOfStart = unixToLocalDateTime(beginBooking),
        dateOfEnd = unixToLocalDateTime(endBooking),
    )

fun List<BookingResponseDTO>.toDomainZone() = map {it.toDomainModel()}
fun emptyUserDTO(id: String, email: String, name: String): UserDTO =
    UserDTO(
        id = id,
        fullName = name,
        active = false,
        role = "",
        avatarUrl = "",
        integrations = null,
        email = email
    )


fun emptyWorkSpaceDTO(id: String) =
    WorkspaceDTO(
        id = id,
        name = "",
        utilities = listOf(),
        zone = null,
        tag = "regular"
    )

fun BookingInfo.toRequestDTOModel(userEmail: String, workSpaceId: String, recurrence: RecurrenceDTO?) =
    BookingRequestDTO(
        ownerEmail = userEmail,
        participantEmails = listOf(userEmail),
        workspaceId = workSpaceId,
        beginBooking = localDateTimeToUnix(dateOfStart)!!,
        endBooking = localDateTimeToUnix(dateOfEnd)!!,
        recurrence = recurrence
    )

fun BookingInfo.toUiModel() = ReservedSeat(
    ownerId = ownerId,
    bookingId = id,
    seatName = seatName,
    bookingDate = dateOfStart.date,
    bookingDay = convertDateTimeToUiDateString(dateOfStart),
    bookingTime = convertDateTimeToUiTimeString(
        startTime = dateOfStart.time,
        endTime = dateOfEnd.time
    )
)
fun List<BookingInfo>.toUIModel() = map { it.toUiModel() }

private fun convertDateTimeToUiDateString(dateOfStart: LocalDateTime) =
    "${capitalizeFirstLetter(dateOfStart.dayOfWeek.localizedBySystem())}, ${dateOfStart.dayOfMonth} ${
        MonthLocalizations.getMonthName(dateOfStart.month, Locale("ru"))}"

private fun convertDateTimeToUiTimeString(
    startTime: LocalTime,
    endTime: LocalTime
) = "${timePad(startTime.hour.toString())}:${timePad(startTime.minute.toString())} " +
        "- ${timePad(endTime.hour.toString())}:${timePad(endTime.minute.toString())}"

fun timePad(time: String) =
    time.padStart(2, '0')