package band.effective.office.elevator.data.database

import band.effective.office.elevator.domain.models.User
import kotlinx.coroutines.flow.Flow

interface DBSource {
    fun getUserFlow(): Flow<User?>
    suspend fun getCurrentUserInfo(): User?

    suspend fun update(user: User, idToken: String)
    suspend fun update(user: User)

    fun deleteUserData()

}