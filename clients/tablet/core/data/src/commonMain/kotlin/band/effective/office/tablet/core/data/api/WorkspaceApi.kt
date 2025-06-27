package band.effective.office.tablet.core.data.api

import band.effective.office.tablet.core.data.model.Either
import band.effective.office.tablet.core.data.model.ErrorResponse
import band.effective.office.tablet.core.data.dto.workspace.WorkspaceDTO
import band.effective.office.tablet.core.data.dto.workspace.WorkspaceZoneDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Interface for workspace-related operations
 */
interface WorkspaceApi {
    /**
     * Get workspace by name
     * @param id workspace name
     * @return Workspace info
     */
    suspend fun getWorkspace(id: String): Either<ErrorResponse, WorkspaceDTO>

    /**
     * Get all workspace current type
     * @param tag workspace type (meeting or regular)
     * @param freeFrom optional start time to check availability
     * @param freeUntil optional end time to check availability
     * @return List of workspaces
     */
    suspend fun getWorkspaces(
        tag: String,
        freeFrom: Long? = null,
        freeUntil: Long? = null
    ): Either<ErrorResponse, List<WorkspaceDTO>>

    /**
     * Get all workspace with bookings current type
     * @param tag workspace type (meeting or regular)
     * @param withBookings whether to include bookings
     * @param freeFrom optional start time to check availability
     * @param freeUntil optional end time to check availability
     * @return List of workspaces with bookings
     */
    suspend fun getWorkspacesWithBookings(
        tag: String,
        withBookings: Boolean = true,
        freeFrom: Long? = null,
        freeUntil: Long? = null
    ): Either<ErrorResponse, List<WorkspaceDTO>>

    /**
     * Get all workspace zones
     * @return List of workspace zones
     */
    suspend fun getZones(): Either<ErrorResponse, List<WorkspaceZoneDTO>>

    /**
     * Subscribe on workspace info updates
     * @param id workspace name
     * @param scope CoroutineScope for collect updates
     * @return Flow with updates
     */
    fun subscribeOnWorkspaceUpdates(
        id: String,
        scope: CoroutineScope
    ): Flow<Either<ErrorResponse, WorkspaceDTO>>
}