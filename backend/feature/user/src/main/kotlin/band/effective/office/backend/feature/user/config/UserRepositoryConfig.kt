package band.effective.office.backend.feature.user.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Configuration for the user repository.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["band.effective.office.backend.feature.user.repository"])
@EntityScan(basePackages = ["band.effective.office.backend.feature.user.repository.entity"])
class UserRepositoryConfig