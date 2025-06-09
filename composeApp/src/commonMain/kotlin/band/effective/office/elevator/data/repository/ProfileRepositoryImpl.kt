package band.effective.office.elevator.data.repository


import band.effective.office.elevator.OfficeElevatorConfig
import band.effective.office.elevator.data.database.DBSource
import band.effective.office.elevator.domain.models.ErrorWithData
import band.effective.office.elevator.domain.models.User
import band.effective.office.elevator.domain.models.toUser
import band.effective.office.elevator.domain.models.toUserDTO
import band.effective.office.elevator.domain.repository.ProfileRepository
import band.effective.office.elevator.utils.map
import band.effective.office.network.api.Api
import band.effective.office.network.dto.UserDTO
import band.effective.office.network.model.Either
import band.effective.office.network.model.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent


class ProfileRepositoryImpl(
    private val api: Api,
    private val bdSource: DBSource
) : ProfileRepository, KoinComponent {

    private val idPhoneNumber = OfficeElevatorConfig.integrationPhoneId
    private val idTelegram = OfficeElevatorConfig.integrationTelegramId

    private val lastResponse: MutableStateFlow<Either<ErrorWithData<User>, User>> =
        MutableStateFlow(
            Either.Error(
                ErrorWithData<User>(
                    ErrorResponse(0, ""), null
                )
            )
        )

    override suspend fun updateUser(user: User): Flow<Either<ErrorWithData<User>, User>> = flow {
        println("User for auth: ${user}")
        val requestResult =
            api.updateUser(user.toUserDTO(idPhoneNumber = idPhoneNumber,
                idTelegram = idTelegram )).convert(this@ProfileRepositoryImpl.lastResponse.value)
        val newUser = requestResult.getData()
        val cashedUser = bdSource.getCurrentUserInfo()
        if (newUser != null && newUser != cashedUser) {
            bdSource.update(newUser)
            lastResponse.update { requestResult }
        }
        val dateForEmit = bdSource.getCurrentUserInfo().packageEither(requestResult)
        emit(dateForEmit)
    }

    //TODO(Artem Gruzdev) maybe can easier this method
    override suspend fun getUser(): Flow<Either<ErrorWithData<User>, User>> = flow {

        val cashedUser = bdSource.getCurrentUserInfo()
        if (cashedUser == null) {
            emit(Either.Error(ErrorWithData(
                error = ErrorResponse(code = 404, description = "you don`t login"),
                saveData = null
            )))
        }
        else {
            val requestResult = api.getUser(cashedUser.id).convert(lastResponse.value)
            val userFromApi = requestResult.getData()
            if (userFromApi != null && userFromApi != cashedUser) {
                bdSource.update(userFromApi)
                lastResponse.update { requestResult }
            }

            val dateForEmit = bdSource.getCurrentUserInfo().packageEither(requestResult)
            emit(dateForEmit)
        }
    }

    private fun Either<ErrorWithData<User>, User>.getData() =
        when (this) {
            is Either.Error -> error.saveData
            is Either.Success -> data
        }

    private fun User?.packageEither(apiResponse: Either<ErrorWithData<User>, User>) =
        when (apiResponse) {
            is Either.Success -> Either.Success(this?:apiResponse.data)
            is Either.Error -> Either.Error(
                ErrorWithData(
                    error = apiResponse.error.error,
                    saveData = this
                )
            )
        }

    private fun Either<ErrorResponse, UserDTO>.convert(
        oldValue: Either<ErrorWithData<User>, User>
    ) =
        map(errorMapper = { error ->
            ErrorWithData(
                error = error, saveData = when (oldValue) {
                    is Either.Error -> oldValue.error.saveData
                    is Either.Success -> oldValue.data
                }
            )
        },
            successMapper = { userDTO ->
                userDTO.toUser()
            })
}