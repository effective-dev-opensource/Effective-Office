package band.effective.office.backend.feature.workspace.core.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("band.effective.office.backend.feature.workspace.core.repository")
@EntityScan("band.effective.office.backend.feature.workspace.core.repository.entity")
class WorkspaceConfig