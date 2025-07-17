package band.effective.office.tablet.core.data.api.impl

import band.effective.office.tablet.core.data.api.Collector
import band.effective.office.tablet.core.data.api.UserApi
import band.effective.office.tablet.core.data.dto.user.UserDTO
import band.effective.office.tablet.core.data.network.HttpClientProvider
import band.effective.office.tablet.core.data.network.HttpRequestUtil
import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Implementation of the UserApi interface
 */
class UserApiImpl(
    private val collector: Collector<String>,
) : UserApi {

    private val client = HttpClientProvider.create()

    override suspend fun getUser(id: String): Either<ErrorResponse, UserDTO> {
        return when (val result = HttpRequestUtil.request<UserDTO>(
            client = client,
            url = "/api/v1/users",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                appendPathSegments(id)
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun getUsers(tag: String): Either<ErrorResponse, List<UserDTO>> {
        return when (val result = HttpRequestUtil.request<List<UserDTO>>(
            client = client,
            url = "/api/v1/users",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                parameters.append("user_tag", tag)
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun updateUser(user: UserDTO): Either<ErrorResponse, UserDTO> {
        return when (val result = HttpRequestUtil.request<UserDTO>(
            client = client,
            url = "/api/v1/users",
            method = HttpRequestUtil.Method.PUT
        ) {
            contentType(ContentType.Application.Json)
            setBody(user)
            url {
                appendPathSegments(user.id)
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun getUserByEmail(email: String): Either<ErrorResponse, UserDTO> {
        return when (val result = HttpRequestUtil.request<UserDTO>(
            client = client,
            url = "/api/v1/users",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                parameters.append(name = "email", value = email)
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override fun subscribeOnOrganizersList(scope: CoroutineScope): Flow<Either<ErrorResponse, List<UserDTO>>> {
        return collector.flow()
            .filter { it == "effectiveoffice-organizer" }
            .map { Either.Success(listOf()) }
    }
}