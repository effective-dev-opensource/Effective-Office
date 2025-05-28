package band.effective.office.elevator.domain.repository

import band.effective.office.elevator.domain.models.ErrorWithData
import band.effective.office.elevator.domain.models.User
import band.effective.office.network.model.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val user: Flow<Either<ErrorWithData<User>, User>>

    suspend fun refresh(): Either<ErrorWithData<User>, User>
    suspend fun updateUser(user: User): Flow<Either<ErrorWithData<User>, User>>
}
