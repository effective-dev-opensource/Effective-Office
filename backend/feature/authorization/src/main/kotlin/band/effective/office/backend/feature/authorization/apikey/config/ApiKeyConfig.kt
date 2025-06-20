package band.effective.office.backend.feature.authorization.apikey.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Configuration for the API key authorization module.
 * This class enables component scanning and JPA repositories for the module.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("band.effective.office.backend.feature.authorization.apikey.repository")
@EntityScan(basePackages = ["band.effective.office.backend.feature.authorization.apikey.entity"])
@ComponentScan("band.effective.office.backend.feature.authorization.apikey")
class ApiKeyConfig
