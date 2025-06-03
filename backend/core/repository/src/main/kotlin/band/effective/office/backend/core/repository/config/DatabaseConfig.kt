package band.effective.office.backend.core.repository.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Configuration for the database and JPA repositories.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["band.effective.office.backend.core.repository"])
@EntityScan(basePackages = ["band.effective.office.backend.repository.entity"])
class DatabaseConfig
