package band.effective.office.tv.repository.duolingo.impl

import band.effective.office.tv.core.network.Either
import band.effective.office.tv.domain.model.duolingo.DuolingoUser
import band.effective.office.tv.domain.model.duolingo.toDomain
import band.effective.office.tv.domain.model.notion.EmploymentType
import band.effective.office.tv.network.duolingo.DuolingoApi
import band.effective.office.tv.repository.duolingo.DuolingoRepository
import band.effective.office.workTogether.Teammate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DuolingoRepositoryImpl @Inject constructor(
    private val duolingoApi: DuolingoApi,
) : DuolingoRepository {
    override suspend fun getUsers(teammates: List<Teammate>): Flow<Either<String, List<DuolingoUser>>> =
        flow {
            val users = teammates.filter {
                it.duolingo != null
                        && it.employment.contains(EmploymentType.Band.value) == true
                        && it.status == "Active"
            }
            var error = false
            val data = users.mapNotNull {
                when (val response = duolingoApi.getUserInfo(it.duolingo!!)) {
                    is Either.Failure -> {
                        emit(Either.Failure(response.error.message))
                        error = true
                        null
                    }

                    is Either.Success -> response.data.toDomain()?.copy(
                        username = it.name.split(' ')[0],
                        photo = it.photo
                    )
                }
            }
            if (!error) {
                emit(Either.Success(data))
            }
        }
}
