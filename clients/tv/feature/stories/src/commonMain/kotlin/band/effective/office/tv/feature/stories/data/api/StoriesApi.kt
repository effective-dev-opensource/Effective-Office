package band.effective.office.tv.feature.stories.data.api

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.stories.data.dto.DuolingoResponseDTO
import band.effective.office.tv.feature.stories.data.dto.SportUserDTO
import band.effective.office.tv.feature.stories.data.dto.SupernovaTalentDTO
import band.effective.office.tv.feature.stories.data.dto.TeammateDTO

/**
 * API interface for Stories feature data sources.
 */
interface StoriesApi {
    /**
     * Get Duolingo users stats by usernames.
     * @param usernames Comma-separated list of Duolingo usernames
     * @return Duolingo users response or error
     */
    suspend fun getDuolingoUsers(usernames: String): Either<ErrorResponse, DuolingoResponseDTO>

    /**
     * Get teammates from Notion.
     * @param active Whether to get only active teammates
     * @return List of teammates or error
     */
    suspend fun getTeammates(active: Boolean = true): Either<ErrorResponse, List<TeammateDTO>>

    /**
     * Get teammates with Supernova scores.
     * @return List of talents with scores or error
     */
    suspend fun getTeammatesSupernova(): Either<ErrorResponse, List<SupernovaTalentDTO>>

    /**
     * Get sport/Clockify users stats.
     * @return List of sport users or error
     */
    suspend fun getSportUsers(): Either<ErrorResponse, List<SportUserDTO>>
}

