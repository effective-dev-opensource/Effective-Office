package band.effective.office.network.api

import band.effective.office.network.dto.BookingDTO
import band.effective.office.network.dto.SuccessResponse
import band.effective.office.network.dto.UserDTO
import band.effective.office.network.dto.WorkspaceDTO
import band.effective.office.network.dto.WorkspaceZoneDTO
import band.effective.office.network.model.Either
import band.effective.office.network.model.ErrorResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
/**Effective office rest api*/
interface Api {
    /**Get workspace by name
     * @param id workspace name
     * @return Workspace info*/
    suspend fun getWorkspace(id: String): Either<ErrorResponse, WorkspaceDTO>

    /**Get all workspace current type
     * @param tag workspace type. Meeting or regular
     * @return List of workspaces*/
    suspend fun getWorkspaces(
        tag: String,
        freeFrom: Long? = null,
        freeUntil: Long? = null
    ): Either<ErrorResponse, List<WorkspaceDTO>>

    suspend fun getZones(): Either<ErrorResponse, List<WorkspaceZoneDTO>>

    /**Get user by id
     * @param id user id
     * @return User info*/
    suspend fun getUser(id: String): Either<ErrorResponse, UserDTO>

    /**Get all users
     * @param tag user type. employee or guest
     * @return Users list*/
    suspend fun getUsers(tag: String): Either<ErrorResponse, List<UserDTO>>

    /**Update information about user
     * @param user new user model
     * @return New model from database*/
    suspend fun updateUser(user: UserDTO): Either<ErrorResponse, UserDTO>

    /**Get booking's by id
     * @param id booking's id
     * @return Information about booking*/
    suspend fun getBooking(id: String): Either<ErrorResponse, BookingDTO>

    /**Get user's bookings
     * @param userId user id whose booking need get
     * @return list bookings current user*/
    suspend fun getBookingsByUser(userId: String): Either<ErrorResponse, List<BookingDTO>>

    /**Get bookings in workspace
     * @param workspaceId workspace id to be reserved
     * @return list bookings in current workspace*/
    suspend fun getBookingsByWorkspaces(workspaceId: String): Either<ErrorResponse, List<BookingDTO>>

    /**Booking workspace
     * @param bookingInfo information for booking workspace
     * @return Created entry from database*/
    suspend fun createBooking(bookingInfo: BookingDTO): Either<ErrorResponse, BookingDTO>

    /**Update booking info
     * @param bookingInfo new information about booking
     * @return new entry from database*/
    suspend fun updateBooking(
        bookingInfo: BookingDTO
    ): Either<ErrorResponse, BookingDTO>

    /**Delete booking
     * @param bookingId id of booking to be deleted
     * @return If complete return SuccessResponse, else return ErrorResponse*/
    suspend fun deleteBooking(
        bookingId: String
    ): Either<ErrorResponse, SuccessResponse>

    /**Subscribe on workspace info updates
     * @param id workspace name
     * @param scope CoroutineScope for collect updates
     * @return Flow with updates*/
    fun subscribeOnWorkspaceUpdates(
        id: String,
        scope: CoroutineScope
    ): Flow<Either<ErrorResponse, WorkspaceDTO>>

    /**Subscribe on organizers list updates
     * @param scope CoroutineScope for collect updates
     * @return Flow with updates*/
    fun subscribeOnOrganizersList(scope: CoroutineScope): Flow<Either<ErrorResponse, List<UserDTO>>>

    /**Subscribe on bookings list updates
     * @param workspaceId workspace name
     * @param scope CoroutineScope for collect updates
     * @return Flow with updates*/
    fun subscribeOnBookingsList(
        workspaceId: String,
        scope: CoroutineScope
    ): Flow<Either<ErrorResponse, List<BookingDTO>>>

    suspend fun getUserByEmail(email: String): Either<ErrorResponse, UserDTO>
}