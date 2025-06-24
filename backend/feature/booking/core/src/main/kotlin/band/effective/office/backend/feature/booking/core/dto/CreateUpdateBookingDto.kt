package band.effective.office.backend.feature.booking.core.dto

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import java.time.Instant

/**
 * Data Transfer Object for creating a new booking.
 */
@Schema(description = "Data for creating a new booking")
data class CreateBookingDto(
    @Schema(description = "Email of the user creating the booking", example = "john.doe@example.com")
    val ownerEmail: String?,

    @Schema(description = "Emails of users participating in the booking", example = "[\"jane.doe@example.com\"]")
    val participantEmails: List<String> = emptyList(),

    @field:NotNull(message = "Workspace ID is required")
    @Schema(description = "ID of the workspace being booked", example = "123e4567-e89b-12d3-a456-426614174000")
    val workspaceId: String,

    @field:NotNull(message = "Begin booking time is required")
    @Schema(description = "Start time of the booking in milliseconds since epoch", example = "1672531200000")
    val beginBooking: Long,

    @field:NotNull(message = "End booking time is required")
    @Schema(description = "End time of the booking in milliseconds since epoch", example = "1672534800000")
    val endBooking: Long,

    @Valid
    @Schema(description = "Recurrence pattern for the booking")
    val recurrence: RecurrenceDto? = null
) {
    /**
     * Converts this DTO to a domain model.
     * Note: This method requires the actual User and Workspace objects to be provided,
     * as the DTO only contains their IDs.
     */
    fun toDomain(
        owner: User,
        participants: List<User>,
        workspace: Workspace
    ): Booking {
        return Booking(
            owner = owner,
            participants = participants,
            workspace = workspace,
            beginBooking = Instant.ofEpochMilli(beginBooking),
            endBooking = Instant.ofEpochMilli(endBooking),
            recurrence = recurrence?.let { RecurrenceDto.toDomain(it) }
        )
    }
}

/**
 * Data Transfer Object for updating an existing booking.
 */
@Schema(description = "Data for updating an existing booking")
data class UpdateBookingDto(
    @Schema(description = "Emails of users participating in the booking", example = "[\"jane.doe@example.com\"]")
    val participantEmails: List<String> = emptyList(),

    @Schema(description = "Start time of the booking in milliseconds since epoch", example = "1672531200000")
    val beginBooking: Long? = null,

    @Schema(description = "End time of the booking in milliseconds since epoch", example = "1672534800000")
    val endBooking: Long? = null,

    @Valid
    @Schema(description = "Recurrence pattern for the booking")
    val recurrence: RecurrenceDto? = null
) {
    /**
     * Converts this DTO to a domain model.
     * Note: This method requires the existing booking to be provided,
     * as the DTO only contains the fields to be updated.
     */
    fun toDomain(
        existingBooking: Booking,
        participants: List<User>
    ): Booking {
        return existingBooking.copy(
            participants = participants,
            beginBooking = beginBooking?.let { Instant.ofEpochMilli(it) } ?: existingBooking.beginBooking,
            endBooking = endBooking?.let { Instant.ofEpochMilli(it) } ?: existingBooking.endBooking,
            recurrence = recurrence?.let { RecurrenceDto.toDomain(it) } ?: existingBooking.recurrence
        )
    }
}
