package band.effective.office.backend.feature.calendar.subscription.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Configuration for the calendar channel repository.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["band.effective.office.backend.feature.calendar.subscription.repository"])
@EntityScan(basePackages = ["band.effective.office.backend.feature.calendar.subscription.repository.entity"])
class ChannelRepositoryConfig