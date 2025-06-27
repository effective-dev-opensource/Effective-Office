package band.effective.office.tablet.core.data.api

import band.effective.office.tablet.core.data.model.Either
import band.effective.office.tablet.core.data.model.ErrorResponse
import band.effective.office.tablet.core.data.dto.user.UserDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Interface for user-related operations
 */
interface UserApi {
    /**
     * Get user by id
     * @param id user id
     * @return User info
     */
    suspend fun getUser(id: String): Either<ErrorResponse, UserDTO>

    /**
     * Get all users
     * @param tag user type (employee or guest)
     * @return Users list
     */
    suspend fun getUsers(tag: String): Either<ErrorResponse, List<UserDTO>>

    /**
     * Update information about user
     * @param user new user model
     * @return New model from database
     */
    suspend fun updateUser(user: UserDTO): Either<ErrorResponse, UserDTO>

    /**
     * Get user by email
     * @param email user email
     * @return User info
     */
    suspend fun getUserByEmail(email: String): Either<ErrorResponse, UserDTO>

    /**
     * Subscribe on organizers list updates
     * @param scope CoroutineScope for collect updates
     * @return Flow with updates
     */
    fun subscribeOnOrganizersList(scope: CoroutineScope): Flow<Either<ErrorResponse, List<UserDTO>>>
}