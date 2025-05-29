package band.effective.office.backend.app.controller

import band.effective.office.backend.app.dto.CreateUserDto
import band.effective.office.backend.app.dto.UpdateUserDto
import band.effective.office.backend.app.dto.UserDto
import band.effective.office.backend.app.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API for managing users")
class UserController(private val userService: UserService) {

    /**
     * Get all users.
     *
     * @return list of all users
     */
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Returns a list of all users in the system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.getAllUsers().map { UserDto.fromDomain(it) }
        return ResponseEntity.ok(users)
    }

    /**
     * Get user by ID.
     *
     * @param id the user ID
     * @return the user if found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Returns a user by their ID"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user")
    @ApiResponse(responseCode = "404", description = "User not found")
    fun getUserById(
        @Parameter(description = "User ID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<UserDto> {
        val user = userService.getUserById(id)
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(UserDto.fromDomain(user))
    }

    /**
     * Get user by username.
     *
     * @param username the username
     * @return the user if found
     */
    @GetMapping("/by-username/{username}")
    @Operation(
        summary = "Get user by username",
        description = "Returns a user by their username"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user")
    @ApiResponse(responseCode = "404", description = "User not found")
    fun getUserByUsername(
        @Parameter(description = "Username", required = true)
        @PathVariable username: String
    ): ResponseEntity<UserDto> {
        val user = userService.getUserByUsername(username)
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(UserDto.fromDomain(user))
    }

    /**
     * Create a new user.
     *
     * @param createUserDto the user data to create
     * @return the created user
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided data"
    )
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    fun createUser(
        @Parameter(description = "User data", required = true)
        @Valid @RequestBody createUserDto: CreateUserDto
    ): ResponseEntity<UserDto> {
        val user = userService.createUser(createUserDto.toDomain())
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.fromDomain(user))
    }

    /**
     * Update an existing user.
     *
     * @param id the user ID
     * @param updateUserDto the updated user data
     * @return the updated user if found
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing user",
        description = "Updates an existing user with the provided data"
    )
    @ApiResponse(responseCode = "200", description = "User successfully updated")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    fun updateUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable id: UUID,
        
        @Parameter(description = "Updated user data", required = true)
        @Valid @RequestBody updateUserDto: UpdateUserDto
    ): ResponseEntity<UserDto> {
        val existingUser = userService.getUserById(id)
            ?: return ResponseEntity.notFound().build()
        
        val updatedUser = userService.updateUser(id, updateUserDto.toDomain(id, existingUser))
            ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(UserDto.fromDomain(updatedUser))
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a user",
        description = "Deletes a user by their ID"
    )
    @ApiResponse(responseCode = "204", description = "User successfully deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    fun deleteUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        val deleted = userService.deleteUser(id)
        
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Get all active users.
     *
     * @return list of active users
     */
    @GetMapping("/active")
    @Operation(
        summary = "Get all active users",
        description = "Returns a list of all active users in the system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active users")
    fun getActiveUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.getActiveUsers().map { UserDto.fromDomain(it) }
        return ResponseEntity.ok(users)
    }
}