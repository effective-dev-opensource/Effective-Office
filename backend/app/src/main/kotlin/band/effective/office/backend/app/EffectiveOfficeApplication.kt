package band.effective.office.backend.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

/**
 * Main application class for the Effective Office application.
 */
@SpringBootApplication
@ComponentScan(basePackages = ["band.effective.office.backend"])
class EffectiveOfficeApplication

/**
 * Main function that starts the Spring Boot application.
 */
fun main(args: Array<String>) {
    runApplication<EffectiveOfficeApplication>(*args)
}