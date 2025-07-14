package band.effective.office.backend.feature.booking.calendar.google

import band.effective.office.backend.core.domain.model.Workspace
import band.effective.office.backend.core.domain.service.WorkspaceDomainService
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/**
 * An implementation of the CalendarIdProvider interface that uses the WorkspaceDomainService.
 * This implementation fetches workspace information from the WorkspaceDomainService
 * and maps workspace IDs to calendar IDs.
 */
@Component
@Primary
class WorkspaceCalendarIdProvider(
    private val workspaceDomainService: WorkspaceDomainService,
) : CalendarIdProvider {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${calendar.default-calendar}")
    private lateinit var defaultCalendar: String

    // In-memory cache of workspace ID to calendar ID mappings
    private val calendarIds = ConcurrentHashMap<UUID, String>()

    /**
     * Returns the calendar ID for a given workspace ID.
     * If the workspace doesn't exist or doesn't have a calendar ID, returns the default calendar.
     *
     * @param workspaceId The ID of the workspace
     * @return The calendar ID for the workspace, or the default calendar if not found
     */
    override fun getCalendarIdByWorkspace(workspaceId: UUID): String {
        // Check if we have a cached calendar ID for this workspace
        val cachedCalendarId = calendarIds[workspaceId]
        if (cachedCalendarId != null) {
            return cachedCalendarId
        }

        try {
            val foundCalendarId = workspaceDomainService.findCalendarIdByWorkspaceId(workspaceId)
            if (foundCalendarId != null) {
                val calendarId = foundCalendarId.calendarId

                // Cache the calendar ID for future use
                calendarIds[workspaceId] = calendarId

                return calendarId
            }
        } catch (e: Exception) {
            logger.warn("Failed to get calendar ID for workspace with ID {}: {}", workspaceId, e.message)
        }

        // If we couldn't get the workspace, or it doesn't have a calendar ID, return the default
        return defaultCalendar
    }

    /**
     * Returns all calendar IDs.
     *
     * @return A list of all calendar IDs
     */
    override fun getAllCalendarIds(): List<String> {
        // Get all workspaces with the "meeting" tag
        val meetingWorkspaces: List<Workspace> = try {
            workspaceDomainService.findAllByTag("meeting")
        } catch (e: Exception) {
            logger.warn("Failed to get meeting workspaces: {}", e.message)
            emptyList()
        }

        // Add calendar IDs for all meeting workspaces
        meetingWorkspaces.forEach { workspace ->
            val workspaceId = workspace.id

            // Skip if we already have this workspace in the cache
            if (calendarIds.containsKey(workspaceId)) {
                return@forEach
            }

            try {
                // First try to get the calendar ID from the repository
                val calendarIdObj = workspaceDomainService.findCalendarIdByWorkspaceId(workspaceId)
                if (calendarIdObj != null) {
                    calendarIds[workspaceId] = calendarIdObj.calendarId
                } else {
                    // If no calendar ID is found, use the workspace name as fallback
                    calendarIds[workspaceId] = workspace.name
                }
            } catch (e: Exception) {
                logger.warn("Failed to get calendar ID for workspace {}: {}", workspaceId, e.message)
                // Use workspace name as fallback
                calendarIds[workspaceId] = workspace.name
            }
        }

        return calendarIds.values.toList()
    }
}
