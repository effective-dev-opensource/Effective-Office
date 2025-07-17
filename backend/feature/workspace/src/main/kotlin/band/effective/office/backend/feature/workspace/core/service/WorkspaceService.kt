package band.effective.office.backend.feature.workspace.core.service

import band.effective.office.backend.core.domain.model.CalendarId
import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.model.WorkspaceZone
import band.effective.office.backend.core.domain.service.WorkspaceDomainService
import band.effective.office.backend.feature.workspace.core.repository.CalendarIdRepository
import band.effective.office.backend.feature.workspace.core.repository.WorkspaceRepository
import band.effective.office.backend.feature.workspace.core.repository.mapper.CalendarIdMapper
import band.effective.office.backend.feature.workspace.core.repository.mapper.WorkspaceMapper
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkspaceService(
    private val repository: WorkspaceRepository,
    private val calendarIdRepository: CalendarIdRepository
) : WorkspaceDomainService {

    /**
     * Retrieves a Workspace model by its id
     *
     * @param id id of requested workspace
     * @return [Workspace] with the given [id] or null if workspace with the given id doesn't exist
     */
    @Transactional(readOnly = true)
    override fun findById(id: UUID): Workspace? {
        return repository.findById(id).getOrNull()?.let { WorkspaceMapper.toDomain(it) }
    }

    /**
     * Returns all workspaces with the given tag
     *
     * @param tag tag name of requested workspaces
     * @return List of [Workspace] with the given [tag]
     */
    @Transactional(readOnly = true)
    override fun findAllByTag(tag: String): List<Workspace> {
        return repository.findAllByTag(tag).map { WorkspaceMapper.toDomain(it) }
    }

    /**
     * Returns all workspace zones
     *
     * @return List of all [WorkspaceZone]
     */
    @Transactional(readOnly = true)
    override fun findAllZones(): List<WorkspaceZone> {
        return repository.findAllWorkspaceZones().map { WorkspaceZone(it.id, it.name) }
    }

    /**
     * Finds a calendar ID by workspace ID
     *
     * @param workspaceId The ID of the workspace
     * @return The calendar ID if found, null otherwise
     */
    @Transactional(readOnly = true)
    override fun findCalendarIdByWorkspaceId(workspaceId: UUID): CalendarId? {
        return calendarIdRepository.findByWorkspaceId(workspaceId)?.let { CalendarIdMapper.toDomain(it) }
    }

    override fun findCalendarEntityById(calendarId: String): CalendarId? {
        return calendarIdRepository.findByCalendarId(calendarId)?.let { return CalendarIdMapper.toDomain(it) }
    }

    override fun findAllCalendarIds(): List<CalendarId> {
        return calendarIdRepository.findAll().map { CalendarIdMapper.toDomain(it) }
    }
}
