package band.effective.office.backend.feature.workspace.core.config

import band.effective.office.backend.core.domain.service.WorkspaceDomainService
import band.effective.office.backend.feature.workspace.core.service.WorkspaceDomainServiceAdapter
import band.effective.office.backend.feature.workspace.core.service.WorkspaceService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for workspace domain services.
 */
@Configuration
class WorkspaceDomainServiceConfig {

    /**
     * Creates a WorkspaceDomainService bean that adapts the WorkspaceService.
     *
     * @param workspaceService The workspace service to adapt
     * @return A WorkspaceDomainService implementation
     */
    @Bean
    fun workspaceDomainService(workspaceService: WorkspaceService): WorkspaceDomainService {
        return WorkspaceDomainServiceAdapter(workspaceService)
    }
}