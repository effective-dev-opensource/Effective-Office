package band.effective.office.tv.feature.stories.data.repository

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.shared.core.domain.asFlow
import band.effective.office.tv.feature.stories.data.api.StoriesApi
import band.effective.office.tv.feature.stories.data.mapper.DuolingoMapper
import band.effective.office.tv.feature.stories.data.mapper.SportMapper
import band.effective.office.tv.feature.stories.data.mapper.SupernovaMapper
import band.effective.office.tv.feature.stories.data.mapper.TeammateMapper
import band.effective.office.tv.feature.stories.domain.model.DuolingoUser
import band.effective.office.tv.feature.stories.domain.model.teammates.Teammate
import band.effective.office.tv.feature.stories.domain.model.sport.ClockifyUser
import band.effective.office.tv.feature.stories.domain.model.supernova.SupernovaScore
import band.effective.office.tv.feature.stories.domain.repository.ClockifyRepository
import band.effective.office.tv.feature.stories.domain.repository.DuolingoRepository
import band.effective.office.tv.feature.stories.domain.repository.NotionRepository
import band.effective.office.tv.feature.stories.domain.repository.SupernovaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf


/**
 * Implementation of [NotionRepository].
 * Fetches teammates data from Notion via backend API.
 */
class NotionRepositoryImpl(private val api: StoriesApi) : NotionRepository {
    override suspend fun getTeammates(): Flow<Either<ErrorResponse, Teammate>> =
        when (val result = api.getTeammates()) {
            is Either.Error -> flowOf(result)
            is Either.Success -> {
                val domainList = result.data.map { TeammateMapper.toDomain(it) }
                Either.Success(domainList).asFlow()
            }
        }
}

/**
 * Implementation of [DuolingoRepository].
 * Fetches Duolingo stats for teammates.
 */
class DuolingoRepositoryImpl(private val api: StoriesApi) : DuolingoRepository {
    override suspend fun getUsers(teammates: List<Teammate>): Flow<Either<ErrorResponse, DuolingoUser>> {
        val usernames = teammates
            .filter { it.duolingo != null && it.employment == "Band" }
            .mapNotNull { it.duolingo }

        if (usernames.isEmpty()) {
            return emptyFlow()
        }

        return when (val result = api.getDuolingoUsers(usernames.joinToString(","))) {
            is Either.Error -> flowOf(result)
            is Either.Success -> {
                val domainList = result.data.users.map { DuolingoMapper.map(it) }
                Either.Success(domainList).asFlow()
            }
        }
    }
}

/**
 * Implementation of [ClockifyRepository].
 * Fetches sport/time tracking data.
 */
class ClockifyRepositoryImpl(private val api: StoriesApi) : ClockifyRepository {
    override suspend fun getTimeEntries(): Flow<Either<ErrorResponse, ClockifyUser>> =
        when (val result = api.getSportUsers()) {
            is Either.Error -> flowOf(result)
            is Either.Success -> Either.Success(result.data.map { SportMapper.toDomain(it) }).asFlow()
        }
}

/**
 * Implementation of [SupernovaRepository].
 * Fetches Supernova scores for teammates.
 */
class SupernovaRepositoryImpl(private val api: StoriesApi) : SupernovaRepository {
    override suspend fun getTeammateScores(): Flow<Either<ErrorResponse, SupernovaScore>> =
        when (val result = api.getTeammatesSupernova()) {
            is Either.Error -> flowOf(result)
            is Either.Success -> Either.Success(result.data.map { SupernovaMapper.toDomain(it) }).asFlow()
        }
}