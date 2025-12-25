package band.effective.office.tv.feature.stories.data.api.impl

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.stories.data.api.StoriesApi
import band.effective.office.tv.feature.stories.data.dto.DuolingoResponseDTO
import band.effective.office.tv.feature.stories.data.dto.SportUserDTO
import band.effective.office.tv.feature.stories.data.dto.SupernovaTalentDTO
import band.effective.office.tv.feature.stories.data.dto.TeammateDTO
import band.effective.office.tv.core.data.network.get
import io.ktor.client.HttpClient

/**
 * Implementation of [StoriesApi] interface.
 */
class StoriesApiImpl(private val client: HttpClient) : StoriesApi {

    override suspend fun getDuolingoUsers(usernames: String): Either<ErrorResponse, DuolingoResponseDTO> =
        get(client, "api/v1/duolingo/users") {
            url.parameters.append("usernames", usernames)
        }

    override suspend fun getTeammates(active: Boolean): Either<ErrorResponse, List<TeammateDTO>> =
        get(client, "api/v1/teammates") {
            url.parameters.append("active", active.toString())
            url.parameters.append("employment", "Band")
            url.parameters.append("employment", "Intern")
        }

    override suspend fun getTeammatesSupernova(): Either<ErrorResponse, List<SupernovaTalentDTO>> =
        get(client, "api/v1/teammates/score")

    override suspend fun getSportUsers(): Either<ErrorResponse, List<SportUserDTO>> =
        get(client, "api/v1/sport")
}
