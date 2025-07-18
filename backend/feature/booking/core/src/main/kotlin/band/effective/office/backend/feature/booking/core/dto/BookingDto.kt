package band.effective.office.backend.feature.booking.core.dto

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.model.Utility
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.model.WorkspaceZone
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import band.effective.office.backend.feature.booking.core.domain.model.RecurrenceModel
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for a booking.
 *
 * This DTO represents a booking of a workspace, including information about the owner,
 * participants, workspace, time period, and recurrence pattern.
 *
 * For recurring bookings, the recurringBookingId field links individual booking instances
 * to their parent recurring booking.
 */
@Schema(description = "Booking information")
data class BookingDto(
    @Schema(description = "User who created the booking")
    val owner: UserDto?,

    @Schema(description = "Users participating in the booking")
    val participants: List<UserDto> = emptyList(),

    @Schema(description = "Workspace being booked")
    val workspace: WorkspaceDto,

    @Schema(description = "Booking ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: String,

    @Schema(description = "Start time of the booking in milliseconds since epoch", example = "1672531200000")
    val beginBooking: Long,

    @Schema(description = "End time of the booking in milliseconds since epoch", example = "1672534800000")
    val endBooking: Long,

    @Schema(description = "Recurrence pattern for the booking")
    val recurrence: RecurrenceDto? = null,

    @Schema(description = "ID of the recurring booking this booking belongs to")
    val recurringBookingId: String? = null,

    @Schema(description = "Flag indicating if booking can be edited/deleted from tablet client", example = "true")
    val isEditable: Boolean = true
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(booking: Booking): BookingDto {
            return BookingDto(
                owner = booking.owner?.let {  UserDto.fromDomain(it) },
                participants = booking.participants.map { UserDto.fromDomain(it) },
                workspace = WorkspaceDto.fromDomain(booking.workspace),
                id = booking.id,
                beginBooking = booking.beginBooking.toEpochMilli(),
                endBooking = booking.endBooking.toEpochMilli(),
                recurrence = booking.recurrence?.let { RecurrenceDto.fromDomain(it) },
                recurringBookingId = booking.recurringBookingId,
                isEditable = booking.isEditable
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
    val id: String,

    @Schema(description = "Full name", example = "John Doe")
    val fullName: String,

    @Schema(description = "Whether the user is active", example = "true")
    val active: Boolean,

    @Schema(description = "User role", example = "USER")
    val role: String,

    @Schema(description = "URL of the user's avatar", example = "https://example.com/avatars/johndoe.png")
    val avatarUrl: String,

    @Schema(description = "User's integrations")
    val integrations: List<IntegrationDto>?,

    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,

    @Schema(description = "User tag", example = "developer")
    val tag: String
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(user: User): UserDto {
            return UserDto(
                id = user.id.toString(),
                fullName = "${user.firstName} ${user.lastName}",
                active = user.active,
                role = user.role,
                avatarUrl = user.avatarUrl,
                integrations = null, // TODO No integrations by default
                email = user.email,
                tag = user.tag,
            )
        }
    }
}

/**
 * Data Transfer Object for an integration.
 */
@Schema(description = "Integration information")
data class IntegrationDto(
    @Schema(description = "Integration type", example = "GOOGLE")
    val type: String,

    @Schema(description = "Integration ID", example = "google123")
    val id: String
)

/**
 * Data Transfer Object for a workspace in a booking context.
 */
@Schema(description = "Workspace information")
data class WorkspaceDto(
    @Schema(description = "Workspace ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: String,

    @Schema(description = "Workspace name", example = "Meeting Room A")
    val name: String,

    @Schema(description = "Utilities available in the workspace")
    val utilities: List<UtilityDto> = emptyList(),

    @Schema(description = "Zone where the workspace is located")
    val zone: WorkspaceZoneDto? = null,

    @Schema(description = "Workspace tag", example = "meeting")
    val tag: String,

    @Schema(description = "Bookings for this workspace")
    val bookings: List<BookingDto>? = null
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(workspace: Workspace): WorkspaceDto {
            return WorkspaceDto(
                id = workspace.id.toString(),
                name = workspace.name,
                utilities = workspace.utilities.map { UtilityDto.fromDomain(it) },
                zone = workspace.zone?.let { WorkspaceZoneDto.fromDomain(it) },
                tag = workspace.tag,
                bookings = null // No bookings by default
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
    val id: String,

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
        fun fromDomain(utility: Utility): UtilityDto {
            return UtilityDto(
                id = utility.id.toString(),
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
    val id: String,

    @Schema(description = "Zone name", example = "Floor 1")
    val name: String
) {
    companion object {
        /**
         * Converts a domain model to a DTO.
         */
        fun fromDomain(zone: WorkspaceZone): WorkspaceZoneDto {
            return WorkspaceZoneDto(
                id = zone.id.toString(),
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
