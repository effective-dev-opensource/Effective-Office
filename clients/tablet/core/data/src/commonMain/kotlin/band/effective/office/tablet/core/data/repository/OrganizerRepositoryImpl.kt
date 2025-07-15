package band.effective.office.tablet.core.data.repository

import band.effective.office.tablet.core.data.api.UserApi
import band.effective.office.tablet.core.data.dto.user.UserDTO
import band.effective.office.tablet.core.data.utils.Converter.toOrganizer
import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.map
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.repository.OrganizerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class OrganizerRepositoryImpl(private val api: UserApi) : OrganizerRepository {
    /**Buffer for organization list*/
    private var orgList: MutableStateFlow<Either<ErrorWithData<List<Organizer>>, List<Organizer>>> =
        MutableStateFlow(
            Either.Error(
                ErrorWithData<List<Organizer>>(
                    ErrorResponse(0, ""), null
                )
            )
        )

    private suspend fun loadOrganizersList(): Either<ErrorWithData<List<Organizer>>, List<Organizer>> =
        with(orgList.value) {
            orgList.update {
                api.getUsers(tag = "employee").convert(this)
            }
            orgList.value
        }

    override suspend fun getOrganizersList(): Either<ErrorWithData<List<Organizer>>, List<Organizer>> =
        with(orgList.value) {
            // NOTE if buffer is empty or contain error get data from api, else get data from buffer
            if (this is Either.Error) {
                val response = loadOrganizersList()
                orgList.update { response }
                response
            } else this
        }


    override fun subscribeOnUpdates(scope: CoroutineScope): Flow<Either<ErrorWithData<List<Organizer>>, List<Organizer>>> =
        flow {
            emit(loadOrganizersList())
        }

    /** Map DTO to domain model
     * @param oldValue past save value*/
    private fun Either<ErrorResponse, List<UserDTO>>.convert(oldValue: Either<ErrorWithData<List<Organizer>>, List<Organizer>>) =
        map(
            errorMapper = { error ->
                ErrorWithData(
                    error = error, saveData = when (oldValue) {
                        is Either.Error -> oldValue.error.saveData
                        is Either.Success -> oldValue.data
                    }
                )
            },
            successMapper = { user ->
                user.filter { it.tag == "employer" }.map { it.toOrganizer() }.sortedBy { it.fullName }
            })
}
