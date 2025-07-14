package band.effective.office.tablet.core.data.api.impl

import band.effective.office.tablet.core.data.api.Collector
import band.effective.office.tablet.core.data.api.WorkspaceApi
import band.effective.office.tablet.core.data.dto.workspace.WorkspaceDTO
import band.effective.office.tablet.core.data.dto.workspace.WorkspaceZoneDTO
import band.effective.office.tablet.core.data.network.HttpClientProvider
import band.effective.office.tablet.core.data.network.HttpRequestUtil
import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Implementation of the WorkspaceApi interface
 */
class WorkspaceApiImpl(
    private val collector: Collector<String>,
) : WorkspaceApi {

    private val client = HttpClientProvider.create()

    override suspend fun getWorkspace(id: String): Either<ErrorResponse, WorkspaceDTO> {
        return when (val workspaces = getWorkspaces("meeting")) {
            is Either.Error -> workspaces
            is Either.Success -> workspaces.data.firstOrNull { it.name == id }.let {
                if (it == null) {
                    Either.Error(ErrorResponse.getResponse(404))
                } else {
                    Either.Success(it)
                }
            }
        }
    }

    override suspend fun getWorkspaces(
        tag: String,
        freeFrom: Long?,
        freeUntil: Long?
    ): Either<ErrorResponse, List<WorkspaceDTO>> {
        return when (val result = HttpRequestUtil.request<List<WorkspaceDTO>>(
            client = client,
            url = "/api/v1/workspaces",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                parameters.append("workspace_tag", tag)
                if (freeFrom != null) {
                    parameters.append("free_from", freeFrom.toString())
                }
                if (freeUntil != null) {
                    parameters.append("free_until", freeUntil.toString())
                }
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun getWorkspacesWithBookings(
        tag: String,
        withBookings: Boolean,
        freeFrom: Long?,
        freeUntil: Long?
    ): Either<ErrorResponse, List<WorkspaceDTO>> {
        return when (val result = HttpRequestUtil.request<List<WorkspaceDTO>>(
            client = client,
            url = "/api/v1/workspaces",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                parameters.append("workspace_tag", tag)
                parameters.append("with_bookings", withBookings.toString())
                if (freeFrom != null) {
                    parameters.append("with_bookings_from", freeFrom.toString())
                }
                if (freeUntil != null) {
                    parameters.append("with_bookings_until", freeUntil.toString())
                }
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun getZones(): Either<ErrorResponse, List<WorkspaceZoneDTO>> {
        return when (val result = HttpRequestUtil.request<List<WorkspaceZoneDTO>>(
            client = client,
            url = "/api/v1/workspaces/zones",
            method = HttpRequestUtil.Method.GET
        )) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override fun subscribeOnWorkspaceUpdates(
        id: String,
        scope: CoroutineScope
    ): Flow<Either<ErrorResponse, WorkspaceDTO>> {
        return collector.flow(scope)
            .filter { it == "workspace" }
            .map {
                Either.Success(
                    WorkspaceDTO(
                        id = "",
                        name = "",
                        utilities = listOf(),
                        zone = null,
                        tag = ""
                    )
                )
            }
    }
}