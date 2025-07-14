package band.effective.office.backend.feature.booking.core.controller

import band.effective.office.backend.core.data.ErrorDto
import band.effective.office.backend.core.domain.service.UserDomainService
import band.effective.office.backend.core.domain.service.WorkspaceDomainService
import band.effective.office.backend.feature.booking.core.dto.BookingDto
import band.effective.office.backend.feature.booking.core.dto.CreateBookingDto
import band.effective.office.backend.feature.booking.core.dto.UpdateBookingDto
import band.effective.office.backend.feature.booking.core.exception.BookingException
import band.effective.office.backend.feature.booking.core.exception.BookingNotFoundException
import band.effective.office.backend.feature.booking.core.exception.InvalidTimeRangeException
import band.effective.office.backend.feature.booking.core.exception.OverlappingBookingException
import band.effective.office.backend.feature.booking.core.exception.UserNotFoundException
import band.effective.office.backend.feature.booking.core.exception.WorkspaceNotFoundException
import band.effective.office.backend.feature.booking.core.service.BookingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
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
@RequestMapping("/v1/bookings")
@Tag(name = "Bookings", description = "API for managing bookings")
class BookingController(
    private val bookingService: BookingService,
    private val userService: UserDomainService,
    private val workspaceService: WorkspaceDomainService,
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
    @ApiResponse(
        responseCode = "404", description = "Booking not found", content = [io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorDto::class)
        )]
    )
    fun getBookingById(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: String
    ): ResponseEntity<BookingDto> {
        val booking = bookingService.getBookingById(id)
            ?: throw BookingNotFoundException("Booking with ID $id not found")

        return ResponseEntity.ok(BookingDto.fromDomain(booking))
    }

    /**
     * Get all bookings with optional filters.
     *
     * @param userId filter by user ID
     * @param workspaceId filter by workspace ID
     * @param from start of the time range
     * @param to end of the time range
     * @param returnInstances whether to return recurring bookings as non-recurrent instances
     * @return list of bookings matching the filters
     */
    @GetMapping
    @Operation(
        summary = "Get all bookings",
        description = "Returns a list of bookings with optional filters. If not specified, the time range defaults to today.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings")
    fun getBookings(
        @Parameter(description = "Filter by user ID")
        @RequestParam(required = false) userId: String?,
        @Parameter(description = "Filter by workspace ID")
        @RequestParam(required = false) workspaceId: String?,
        @Parameter(description = "Start of the time range in milliseconds since epoch", example = "1672531200000")
        @RequestParam(required = false) from: Long? = null,
        @Parameter(description = "End of the time range in milliseconds since epoch", example = "1675209599000")
        @RequestParam(required = false) to: Long? = null,
        @Parameter(description = "Whether to return recurring bookings as non-recurrent instances", example = "true")
        @RequestParam(required = false, defaultValue = "true") returnInstances: Boolean
    ): ResponseEntity<List<BookingDto>> {
        // Convert String IDs to UUIDs if provided
        val userUuid = userId?.let {
            runCatching { UUID.fromString(userId) }.getOrNull()
                ?: throw UserNotFoundException("Invalid username ID format: $it")
        }

        val workspaceUuid = workspaceId?.let {
            runCatching { UUID.fromString(workspaceId) }.getOrNull()
                ?: throw WorkspaceNotFoundException("Invalid workspace ID format: $it")
        }

        // If from is not provided, default to the start of today
        val effectiveFrom = from?.let { Instant.ofEpochMilli(it) }
            ?: LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()

        // If to is not provided, default to the end of today
        val effectiveTo = to?.let { Instant.ofEpochMilli(it) }
            ?: LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant()

        val bookings = when {
            userUuid != null && workspaceUuid != null -> {
                bookingService.getBookingsByUserAndWorkspace(
                    userUuid,
                    workspaceUuid,
                    effectiveFrom,
                    effectiveTo,
                    returnInstances
                )
            }

            userUuid != null -> {
                bookingService.getBookingsByUser(userUuid, effectiveFrom, effectiveTo, returnInstances)
            }

            workspaceUuid != null -> {
                bookingService.getBookingsByWorkspace(workspaceUuid, effectiveFrom, effectiveTo, returnInstances)
            }

            else -> {
                bookingService.getAllBookings(effectiveFrom, effectiveTo, returnInstances)
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
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = [io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorDto::class)
        )]
    )
    @ApiResponse(
        responseCode = "404",
        description = "User or workspace not found",
        content = [io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorDto::class)
        )]
    )
    fun createBooking(
        @Parameter(description = "Booking data", required = true)
        @Valid @RequestBody createBookingDto: CreateBookingDto
    ): ResponseEntity<BookingDto> {
        // Get the owner user by email if provided, otherwise create a system user
        val owner = createBookingDto.ownerEmail?.let {
            userService.findByEmail(createBookingDto.ownerEmail)
                ?: throw UserNotFoundException("Owner with email ${createBookingDto.ownerEmail} not found")
        }

        // Get the participants by email
        val participants = createBookingDto.participantEmails.mapNotNull { email ->
            userService.findByEmail(email)
                ?: run {
                    // Log warning but don't fail if participant not found
                    println("Warning: Participant with email $email not found")
                    null
                }
        }

        // Convert workspaceId from String to UUID
        val workspaceUuid = try {
            UUID.fromString(createBookingDto.workspaceId)
        } catch (e: IllegalArgumentException) {
            throw WorkspaceNotFoundException("Invalid workspace ID format: ${createBookingDto.workspaceId}")
        }

        val workspace = workspaceService.findById(workspaceUuid)
            ?: throw WorkspaceNotFoundException("Workspace with ID ${createBookingDto.workspaceId} not found")

        // Compare timestamps
        if (createBookingDto.beginBooking >= createBookingDto.endBooking) {
            throw InvalidTimeRangeException()
        }

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
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = [io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorDto::class)
        )]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Booking or participants not found",
        content = [io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorDto::class)
        )]
    )
    fun updateBooking(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: String,

        @Parameter(description = "Updated booking data", required = true)
        @Valid @RequestBody updateBookingDto: UpdateBookingDto
    ): ResponseEntity<BookingDto> {
        val existingBooking = bookingService.getBookingById(id)
            ?: throw BookingNotFoundException("Booking with ID $id not found")

        // Get the participants by email
        val participants = updateBookingDto.participantEmails.mapNotNull { email ->
            userService.findByEmail(email)
                ?: run {
                    // Log warning but don't fail if participant not found
                    println("Warning: Participant with email $email not found")
                    null
                }
        }

        // Get the owner user by email if provided, otherwise create a system user
        val owner = updateBookingDto.ownerEmail?.let {
            userService.findByEmail(updateBookingDto.ownerEmail)
                ?: throw UserNotFoundException("Owner with email ${updateBookingDto.ownerEmail} not found")
        }

        // Convert workspaceId from String to UUID
        val workspaceUuid = try {
            UUID.fromString(updateBookingDto.workspaceId)
        } catch (e: IllegalArgumentException) {
            throw WorkspaceNotFoundException("Invalid workspace ID format: ${updateBookingDto.workspaceId}")
        }

        val workspace = workspaceService.findById(workspaceUuid)
            ?: throw WorkspaceNotFoundException("Workspace with ID ${updateBookingDto.workspaceId} not found")


        // Convert DTO to a domain model and update the booking
        val booking = updateBookingDto.toDomain(existingBooking, participants, owner, workspace)
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
    @ApiResponse(
        responseCode = "404", description = "Booking not found", content = [io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorDto::class)
        )]
    )
    fun deleteBooking(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: String
    ): ResponseEntity<Void> {
        val booking = bookingService.getBookingById(id)
            ?: throw BookingNotFoundException("Booking with ID $id not found")

        bookingService.deleteBooking(booking)
        return ResponseEntity.noContent().build()
    }

    /**
     * Exception handler for BookingNotFoundException.
     */
    @ExceptionHandler(BookingNotFoundException::class)
    fun handleBookingNotFoundException(ex: BookingNotFoundException): ResponseEntity<ErrorDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for UserNotFoundException.
     */
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for WorkspaceNotFoundException.
     */
    @ExceptionHandler(WorkspaceNotFoundException::class)
    fun handleWorkspaceNotFoundException(ex: WorkspaceNotFoundException): ResponseEntity<ErrorDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for InvalidTimeRangeException.
     */
    @ExceptionHandler(InvalidTimeRangeException::class)
    fun handleInvalidTimeRangeException(ex: InvalidTimeRangeException): ResponseEntity<ErrorDto> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Exception handler for OverlappingBookingException.
     */
    @ExceptionHandler(OverlappingBookingException::class)
    fun handleOverlappingBookingException(ex: OverlappingBookingException): ResponseEntity<ErrorDto> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }

    /**
     * Generic exception handler for BookingException.
     * This handler catches any BookingException that doesn't have a more specific handler.
     */
    @ExceptionHandler(BookingException::class)
    fun handleBookingException(ex: BookingException): ResponseEntity<ErrorDto> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )
        )
    }
}
