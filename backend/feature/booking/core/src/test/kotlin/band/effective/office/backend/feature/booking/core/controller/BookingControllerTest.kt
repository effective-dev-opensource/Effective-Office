package band.effective.office.backend.feature.booking.core.controller

import band.effective.office.backend.app.EffectiveOfficeApplication
import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.feature.booking.core.domain.model.Booking
import band.effective.office.backend.feature.booking.core.dto.CreateBookingDto
import band.effective.office.backend.feature.booking.core.service.BookingService
import band.effective.office.backend.feature.user.service.UserService
import band.effective.office.backend.feature.workspace.core.service.WorkspaceService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.*
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@TestPropertySource(properties = [
    "calendar.application-name=TestApp",
    "calendar.delegated-user=test-user@example.com",
    "calendar.default-calendar=/dev/null"
])
@SpringBootTest(
    classes = [EffectiveOfficeApplication::class, BookingController::class],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BookingControllerTest {


    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookingService: BookingService

    @MockitoBean
    private lateinit var workspaceService: WorkspaceService

    @MockitoBean
    private lateinit var  userService: UserService

    private val mapper = jacksonObjectMapper()

    private fun dummyUser(id: UUID = UUID.randomUUID()): User = User(
        id = id,
        username = "johndoe",
        email = "john.doe@example.com",
        firstName = "John",
        lastName = "Doe",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        active = true,
        role = "USER",
        avatarUrl = "https://example.com/avatar.png",
        tag = "developer"
    )

    private fun dummyWorkspace(id: UUID = UUID.randomUUID()) = Workspace(
        id = id,
        name = "Meeting Room",
        utilities = emptyList(),
        zone = null,
        tag = "meeting"
    )

    private fun dummyBooking(): Booking {
        val user = dummyUser()
        val workspace = dummyWorkspace()
        val now = Instant.now()
        return Booking(
            id = UUID.randomUUID().toString(),
            owner = user,
            participants = listOf(user),
            workspace = workspace,
            beginBooking = now,
            endBooking = now.plusSeconds(3600)
        )
    }

    @Test
    fun `GET booking by id returns 200`() {
        val booking = dummyBooking()
        given(bookingService.getBookingById(booking.id)).willReturn(booking)

        mockMvc.get("/v1/bookings/${booking.id}")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(booking.id) }
            }

        verify(bookingService).getBookingById(booking.id)
    }

    @Test
    fun `GET booking by id returns 404 if not found`() {
        val id = UUID.randomUUID().toString()
        given(bookingService.getBookingById(id)).willReturn(null)

        mockMvc.get("/v1/bookings/$id")
            .andExpect {
                status { isNotFound() }
            }

        verify(bookingService).getBookingById(id)
    }

    @Test
    fun `POST create booking returns 201`() {
        val booking = dummyBooking()
        val dto = CreateBookingDto(
            ownerEmail = booking.owner?.email,
            participantEmails = booking.participants.map { it.email },
            workspaceId = booking.workspace.id.toString(),
            beginBooking = booking.beginBooking.toEpochMilli(),
            endBooking = booking.endBooking.toEpochMilli(),
            recurrence = null
        )

        given(userService.findByEmail(any())).willReturn(booking.owner)
        given(workspaceService.findById(UUID.fromString(dto.workspaceId))).willReturn(booking.workspace)
        given(bookingService.createBooking(any())).willReturn(booking)

        mockMvc.post("/v1/bookings") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(dto)
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { value(booking.id) }
                jsonPath("$.owner.email") { value(dto.ownerEmail) }
            }

        verify(bookingService).createBooking(any())
    }

    @Test
    fun `DELETE booking returns 204`() {
        val booking = dummyBooking()
        given(bookingService.getBookingById(booking.id)).willReturn(booking)

        mockMvc.delete("/v1/bookings/${booking.id}")
            .andExpect {
                status { isNoContent() }
            }

        verify(bookingService).deleteBooking(booking)
    }

    @Test
    fun `DELETE booking returns 404 when not found`() {
        val id = UUID.randomUUID().toString()
        given(bookingService.getBookingById(id)).willReturn(null)

        mockMvc.delete("/v1/bookings/$id")
            .andExpect {
                status { isNotFound() }
            }

        verify(bookingService).getBookingById(id)
    }
}