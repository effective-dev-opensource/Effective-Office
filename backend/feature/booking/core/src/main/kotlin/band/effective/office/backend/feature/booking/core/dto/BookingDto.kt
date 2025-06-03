package band.effective.office.backend.feature.booking.core.dto

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import band.effective.office.backend.feature.booking.core.domain.model.RecurrenceModel
import band.effective.office.backend.feature.booking.core.domain.model.Workspace
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

/**
 * Data Transfer Object for a booking.
 */
@Schema(description = "Booking information")
data class BookingDto(
    @Schema(description = "Booking ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,

    @Schema(description = "User who created the booking")
    val owner: UserDto,

    @Schema(description = "Users participating in the booking")
    val participants: List<UserDto> = emptyList(),

    @Schema(description = "Workspace being booked")
    val workspace: WorkspaceDto,

    @Schema(description = "Start time of the booking", example = "2023-01-01T10:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val beginBooking: Instant,

    @Schema(description = "End time of the booking", example = "2023-01-01T11:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val endBooking: Instant,

    @Schema(description = "Recurrence pattern for the booking")
    val recurrence: RecurrenceDto? = null,

    @Schema(description = "External event ID from the calendar provider")
    val externalEventId: String? = null
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(booking: Booking): BookingDto {
            return BookingDto(
                id = booking.id,
                owner = UserDto.fromDomain(booking.owner),
                participants = booking.participants.map { UserDto.fromDomain(it) },
                workspace = WorkspaceDto.fromDomain(booking.workspace),
                beginBooking = booking.beginBooking,
                endBooking = booking.endBooking,
                recurrence = booking.recurrence?.let { RecurrenceDto.fromDomain(it) },
                externalEventId = booking.externalEventId
            )
        }
    }
}

/**
 * Data Transfer Object for a user in a booking context.
 */
@Schema(description = "User information")
data class UserDto(
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,

    @Schema(description = "Username", example = "johndoe")
    val username: String,

    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,

    @Schema(description = "First name", example = "John")
    val firstName: String,

    @Schema(description = "Last name", example = "Doe")
    val lastName: String
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(user: User): UserDto {
            return UserDto(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName
            )
        }
    }
}

/**
 * Data Transfer Object for a workspace in a booking context.
 */
@Schema(description = "Workspace information")
data class WorkspaceDto(
    @Schema(description = "Workspace ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,

    @Schema(description = "Workspace name", example = "Meeting Room A")
    val name: String,

    @Schema(description = "Workspace tag", example = "meeting")
    val tag: String,

    @Schema(description = "Utilities available in the workspace")
    val utilities: List<UtilityDto> = emptyList(),

    @Schema(description = "Zone where the workspace is located")
    val zone: WorkspaceZoneDto? = null
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(workspace: Workspace): WorkspaceDto {
            return WorkspaceDto(
                id = workspace.id,
                name = workspace.name,
                tag = workspace.tag,
                utilities = workspace.utilities.map { UtilityDto.fromDomain(it) },
                zone = workspace.zone?.let { WorkspaceZoneDto.fromDomain(it) }
            )
        }
    }
}

/**
 * Data Transfer Object for a utility in a workspace.
 */
@Schema(description = "Utility information")
data class UtilityDto(
    @Schema(description = "Utility ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,

    @Schema(description = "Utility name", example = "Projector")
    val name: String,

    @Schema(description = "URL of the utility icon", example = "https://example.com/icons/projector.png")
    val iconUrl: String,

    @Schema(description = "Number of this utility available", example = "1")
    val count: Int
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(utility: band.effective.office.backend.feature.booking.core.domain.model.Utility): UtilityDto {
            return UtilityDto(
                id = utility.id,
                name = utility.name,
                iconUrl = utility.iconUrl,
                count = utility.count
            )
        }
    }
}

/**
 * Data Transfer Object for a workspace zone.
 */
@Schema(description = "Workspace zone information")
data class WorkspaceZoneDto(
    @Schema(description = "Zone ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID,

    @Schema(description = "Zone name", example = "Floor 1")
    val name: String
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(zone: band.effective.office.backend.feature.booking.core.domain.model.WorkspaceZone): WorkspaceZoneDto {
            return WorkspaceZoneDto(
                id = zone.id,
                name = zone.name
            )
        }
    }
}

/**
 * Data Transfer Object for a recurrence pattern.
 */
@Schema(description = "Recurrence pattern information")
data class RecurrenceDto(
    @Schema(description = "Interval between recurrences", example = "1")
    val interval: Int? = null,

    @Schema(description = "Frequency of recurrence", example = "DAILY")
    val freq: String,

    @Schema(description = "Number of recurrences", example = "10")
    val count: Int? = null,

    @Schema(description = "End date of the recurrence (timestamp)", example = "1672531200000")
    val until: Long? = null,

    @Schema(description = "Days of the week for recurrence", example = "[1, 3, 5]")
    val byDay: List<Int> = emptyList(),

    @Schema(description = "Months for recurrence", example = "[1, 6]")
    val byMonth: List<Int> = emptyList(),

    @Schema(description = "Days of the year for recurrence", example = "[1, 100, 200]")
    val byYearDay: List<Int> = emptyList(),

    @Schema(description = "Hours for recurrence", example = "[9, 14]")
    val byHour: List<Int> = emptyList()
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(recurrence: RecurrenceModel): RecurrenceDto {
            return RecurrenceDto(
                interval = recurrence.interval,
                freq = recurrence.freq,
                count = recurrence.count,
                until = recurrence.until,
                byDay = recurrence.byDay,
                byMonth = recurrence.byMonth,
                byYearDay = recurrence.byYearDay,
                byHour = recurrence.byHour
            )
        }

        /**
         * Converts a DTO to a domain model.
         */
        fun toDomain(dto: RecurrenceDto): RecurrenceModel {
            return RecurrenceModel(
                interval = dto.interval,
                freq = dto.freq,
                count = dto.count,
                until = dto.until,
                byDay = dto.byDay,
                byMonth = dto.byMonth,
                byYearDay = dto.byYearDay,
                byHour = dto.byHour
            )
        }
    }
}