package band.effective.office.backend.feature.user.controller

import band.effective.office.backend.app.EffectiveOfficeApplication
import band.effective.office.backend.feature.user.service.UserService
import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.feature.user.dto.CreateUserDto
import band.effective.office.backend.feature.user.dto.UpdateUserDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.*
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest(
    classes = [EffectiveOfficeApplication::class, UserController::class],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userService: UserService

    private val mapper = jacksonObjectMapper()

    private fun makeDummyUser(id: UUID = UUID.randomUUID()): User = User(
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

    @Test
    fun `GET all users returns list`() {
        val user = makeDummyUser()
        given(userService.getAllUsers()).willReturn(listOf(user))

        mockMvc.get("/v1/users") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(1) }
                jsonPath("$[0].id")       { value(user.id.toString()) }
                jsonPath("$[0].fullName") { value("${user.firstName} ${user.lastName}") }
                jsonPath("$[0].email")    { value(user.email) }
            }

        verify(userService).getAllUsers()
    }

    @Test
    fun `GET user by id returns 200 when found`() {
        val user = makeDummyUser()
        given(userService.getUserById(user.id)).willReturn(user)

        mockMvc.get("/v1/users/${user.id}") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.id")       { value(user.id.toString()) }
                jsonPath("$.fullName") { value("${user.firstName} ${user.lastName}") }
                jsonPath("$.email")    { value(user.email) }
            }

        verify(userService).getUserById(user.id)
    }

    @Test
    fun `GET user by id returns 404 when not found`() {
        val id = UUID.randomUUID()
        given(userService.getUserById(id)).willReturn(null)

        mockMvc.get("/v1/users/$id")
            .andExpect { status { isNotFound() } }

        verify(userService).getUserById(id)
    }

    @Test
    fun `GET by-username returns 200 when found`() {
        val user = makeDummyUser()
        given(userService.getUserByUsername(user.username)).willReturn(user)

        mockMvc.get("/v1/users/by-username/${user.username}") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.fullName") { value("${user.firstName} ${user.lastName}") }
                jsonPath("$.email")    { value(user.email) }
            }

        verify(userService).getUserByUsername(user.username)
    }

    @Test
    fun `GET by-username returns 404 when not found`() {
        given(userService.getUserByUsername("nope")).willReturn(null)

        mockMvc.get("/v1/users/by-username/nope")
            .andExpect { status { isNotFound() } }

        verify(userService).getUserByUsername("nope")
    }

    @Test
    fun `GET active users returns only active`() {
        val user = makeDummyUser().copy(active = true)
        given(userService.getActiveUsers()).willReturn(listOf(user))

        mockMvc.get("/v1/users/active") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(1) }
                jsonPath("$[0].active") { value(true) }
            }

        verify(userService).getActiveUsers()
    }

    @Test
    fun `POST create user returns 201 and body`() {
        val user = makeDummyUser().copy(id = UUID.randomUUID())
        val dto = CreateUserDto(
            email    = user.email,
            fullName = "${user.firstName} ${user.lastName}",
            role     = user.role,
            avatarUrl= user.avatarUrl,
            tag      = user.tag
        )
        given(userService.createUser(any())).willReturn(user)

        mockMvc.post("/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content     = mapper.writeValueAsString(dto)
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id")       { value(user.id.toString()) }
                jsonPath("$.fullName") { value(dto.fullName) }
                jsonPath("$.email")    { value(dto.email) }
            }

        verify(userService).createUser(argThat {
            email     == dto.email &&
                    firstName == dto.fullName.substringBefore(" ") &&
                    lastName  == dto.fullName.substringAfter(" ") &&
                    role      == dto.role &&
                    avatarUrl == dto.avatarUrl &&
                    tag       == dto.tag
        })
    }

    @Test
    fun `PUT update user returns 200 when exists`() {
        val id = UUID.randomUUID()
        val existing = makeDummyUser(id)
        given(userService.getUserById(id)).willReturn(existing)

        val updated = existing.copy(firstName = "Jane", lastName = "Roe", active = false)
        val dto = UpdateUserDto(
            email    = updated.email,
            fullName = "${updated.firstName} ${updated.lastName}",
            role     = updated.role,
            avatarUrl= updated.avatarUrl,
            tag      = updated.tag,
            active   = updated.active
        )
        given(userService.updateUser(eq(id), any())).willReturn(updated)

        mockMvc.put("/v1/users/$id") {
            contentType = MediaType.APPLICATION_JSON
            content     = mapper.writeValueAsString(dto)
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.fullName") { value(dto.fullName) }
                jsonPath("$.active")   { value(false) }
            }

        verify(userService).updateUser(eq(id), argThat {
            email     == dto.email &&
                    firstName == dto.fullName.substringBefore(" ") &&
                    lastName  == dto.fullName.substringAfter(" ") &&
                    active    == dto.active
        })
    }

    @Test
    fun `DELETE user returns 204 when deleted`() {
        val id = UUID.randomUUID()
        given(userService.deleteUser(id)).willReturn(true)

        mockMvc.delete("/v1/users/$id")
            .andExpect { status { isNoContent() } }

        verify(userService).deleteUser(id)
    }

    @Test
    fun `DELETE user returns 404 when not found`() {
        val id = UUID.randomUUID()
        given(userService.deleteUser(id)).willReturn(false)

        mockMvc.delete("/v1/users/$id")
            .andExpect { status { isNotFound() } }

        verify(userService).deleteUser(id)
    }
}