package band.effective.office.backend.feature.booking.core.controller

import band.effective.office.backend.core.domain.service.UserDomainService
import band.effective.office.backend.feature.booking.core.domain.model.Workspace
import band.effective.office.backend.feature.booking.core.dto.BookingDto
import band.effective.office.backend.feature.booking.core.dto.CreateBookingDto
import band.effective.office.backend.feature.booking.core.dto.UpdateBookingDto
import band.effective.office.backend.feature.booking.core.service.BookingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.time.Instant
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for managing bookings.
 */
@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "API for managing bookings")
class BookingController(
    private val bookingService: BookingService,
    private val userService: UserDomainService
    // In a real implementation, we would need a service for retrieving Workspace objects
    // private val workspaceService: WorkspaceService
) {

    /**
     * Get a booking by ID.
     *
     * @param id the booking ID
     * @return the booking if found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get booking by ID",
        description = "Returns a booking by its ID",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved booking")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    fun getBookingById(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<BookingDto> {
        val booking = bookingService.getBookingById(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(BookingDto.fromDomain(booking))
    }

    /**
     * Get all bookings with optional filters.
     *
     * @param userId filter by user ID
     * @param workspaceId filter by workspace ID
     * @param from start of the time range
     * @param to end of the time range
     * @return list of bookings matching the filters
     */
    @GetMapping
    @Operation(
        summary = "Get all bookings",
        description = "Returns a list of bookings with optional filters",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings")
    fun getBookings(
        @Parameter(description = "Filter by user ID")
        @RequestParam(required = false) userId: UUID?,

        @Parameter(description = "Filter by workspace ID")
        @RequestParam(required = false) workspaceId: UUID?,

        @Parameter(description = "Start of the time range", example = "2023-01-01T00:00:00Z")
        @RequestParam(required = true) from: Instant,

        @Parameter(description = "End of the time range", example = "2023-01-31T23:59:59Z")
        @RequestParam(required = false) to: Instant?
    ): ResponseEntity<List<BookingDto>> {
        val bookings = when {
            userId != null && workspaceId != null -> {
                bookingService.getBookingsByUserAndWorkspace(userId, workspaceId, from, to)
            }

            userId != null -> {
                bookingService.getBookingsByUser(userId, from, to)
            }

            workspaceId != null -> {
                bookingService.getBookingsByWorkspace(workspaceId, from, to)
            }

            else -> {
                // In a real implementation, we would need a method to get all bookings
                // For now, we'll return an empty list
                emptyList()
            }
        }

        return ResponseEntity.ok(bookings.map { BookingDto.fromDomain(it) })
    }

    /**
     * Create a new booking.
     *
     * @param createBookingDto the booking data
     * @return the created booking
     */
    @PostMapping
    @Operation(
        summary = "Create a new booking",
        description = "Creates a new booking with the provided data",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "201", description = "Booking successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "User or workspace not found")
    fun createBooking(
        @Parameter(description = "Booking data", required = true)
        @Valid @RequestBody createBookingDto: CreateBookingDto
    ): ResponseEntity<BookingDto> {
        // Get the owner user
        val owner = userService.findById(createBookingDto.ownerId)
            ?: return ResponseEntity.notFound().build()

        // Get the participants
        val participants = createBookingDto.participantIds.mapNotNull { userId ->
            userService.findById(userId)
        }

        // In a real implementation, we would get the workspace from a service
        // val workspace = workspaceService.getWorkspaceById(createBookingDto.workspaceId)
        //     ?: return ResponseEntity.notFound().build()

        // For now, create a stub workspace
        val workspace = Workspace(
            id = createBookingDto.workspaceId,
            name = createBookingDto.workspaceId.toString(),
            tag = "meeting"
        )

        // Convert DTO to a domain model and create the booking
        val booking = createBookingDto.toDomain(owner, participants, workspace)
        val createdBooking = bookingService.createBooking(booking)

        return ResponseEntity.status(201).body(BookingDto.fromDomain(createdBooking))
    }

    /**
     * Update an existing booking.
     *
     * @param id the booking ID
     * @param updateBookingDto the updated booking data
     * @return the updated booking
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a booking",
        description = "Updates an existing booking with the provided data",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Booking successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Booking or participants not found")
    fun updateBooking(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: UUID,

        @Parameter(description = "Updated booking data", required = true)
        @Valid @RequestBody updateBookingDto: UpdateBookingDto
    ): ResponseEntity<BookingDto> {
        // Get the existing booking
        val existingBooking = bookingService.getBookingById(id)
            ?: return ResponseEntity.notFound().build()

        // Get the participants
        val participants = updateBookingDto.participantIds.mapNotNull { userId ->
            userService.findById(userId)
        }

        // Convert DTO to a domain model and update the booking
        val booking = updateBookingDto.toDomain(existingBooking, participants)
        val updatedBooking = bookingService.updateBooking(booking)

        return ResponseEntity.ok(BookingDto.fromDomain(updatedBooking))
    }

    /**
     * Delete a booking by ID.
     *
     * @param id the booking ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a booking",
        description = "Deletes a booking by its ID",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "204", description = "Booking successfully deleted")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    fun deleteBooking(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        val deleted = bookingService.deleteBookingById(id)

        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
