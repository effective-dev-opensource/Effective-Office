package band.effective.office.tv.feature.stories.domain.repository

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.stories.domain.model.DuolingoUser
import band.effective.office.tv.feature.stories.domain.model.teammates.Teammate
import band.effective.office.tv.feature.stories.domain.model.sport.ClockifyUser
import band.effective.office.tv.feature.stories.domain.model.supernova.SupernovaScore
import kotlinx.coroutines.flow.Flow

/**
 * Repository for fetching teammates from Notion.
 */
interface NotionRepository {
    /**
     * Get all active teammates.
     * @return Flow of Either with teammates or error (one teammate per emission)
     */
    suspend fun getTeammates(): Flow<Either<ErrorResponse, Teammate>>
}

/**
 * Repository for fetching Duolingo stats.
 */
interface DuolingoRepository {
    /**
     * Get Duolingo stats for given teammates.
     * @param teammates List of teammates to fetch Duolingo data for
     * @return Flow of Either with Duolingo users or error (one user per emission)
     */
    suspend fun getUsers(teammates: List<Teammate>): Flow<Either<ErrorResponse, DuolingoUser>>
}

/**
 * Repository for fetching Clockify/sport time entries.
 */
interface ClockifyRepository {
    /**
     * Get sport time entries for all users.
     * @return Flow of Either with Clockify users or error (one user per emission)
     */
    suspend fun getTimeEntries(): Flow<Either<ErrorResponse, ClockifyUser>>
}

/**
 * Repository for fetching Supernova scores.
 */
interface SupernovaRepository {
    /**
     * Get Supernova scores for teammates.
     * @return Flow of Either with talents or error (one talent per emission)
     */
    suspend fun getTeammateScores(): Flow<Either<ErrorResponse, SupernovaScore>>
}


