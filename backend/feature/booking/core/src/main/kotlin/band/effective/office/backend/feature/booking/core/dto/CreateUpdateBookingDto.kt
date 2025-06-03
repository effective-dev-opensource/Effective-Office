package band.effective.office.backend.feature.booking.core.dto

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import band.effective.office.backend.feature.booking.core.domain.model.Workspace
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.UUID

/**
 * Data Transfer Object for creating a new booking.
 */
@Schema(description = "Data for creating a new booking")
data class CreateBookingDto(
    @field:NotNull(message = "Owner ID is required")
    @Schema(description = "ID of the user creating the booking", example = "123e4567-e89b-12d3-a456-426614174000")
    val ownerId: UUID,

    @Schema(description = "IDs of users participating in the booking", example = "[\"123e4567-e89b-12d3-a456-426614174000\"]")
    val participantIds: List<UUID> = emptyList(),

    @field:NotNull(message = "Workspace ID is required")
    @Schema(description = "ID of the workspace being booked", example = "123e4567-e89b-12d3-a456-426614174000")
    val workspaceId: UUID,

    @field:NotNull(message = "Begin booking time is required")
    @Schema(description = "Start time of the booking", example = "2023-01-01T10:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val beginBooking: Instant,

    @field:NotNull(message = "End booking time is required")
    @Schema(description = "End time of the booking", example = "2023-01-01T11:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val endBooking: Instant,

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
            beginBooking = beginBooking,
            endBooking = endBooking,
            recurrence = recurrence?.let { RecurrenceDto.toDomain(it) }
        )
    }
}

/**
 * Data Transfer Object for updating an existing booking.
 */
@Schema(description = "Data for updating an existing booking")
data class UpdateBookingDto(
    @Schema(description = "IDs of users participating in the booking", example = "[\"123e4567-e89b-12d3-a456-426614174000\"]")
    val participantIds: List<UUID> = emptyList(),

    @Schema(description = "Start time of the booking", example = "2023-01-01T10:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val beginBooking: Instant? = null,

    @Schema(description = "End time of the booking", example = "2023-01-01T11:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val endBooking: Instant? = null,

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
            beginBooking = beginBooking ?: existingBooking.beginBooking,
            endBooking = endBooking ?: existingBooking.endBooking,
            recurrence = recurrence?.let { RecurrenceDto.toDomain(it) } ?: existingBooking.recurrence
        )
    }
}