package band.effective.office.backend.app.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for OpenAPI documentation.
 */
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "Token",
    `in` = SecuritySchemeIn.HEADER
)
class OpenAPIConfig {

    /**
     * Configures the OpenAPI documentation.
     *
     * @return The OpenAPI configuration.
     */
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Effective Office API")
                    .description("API for the Effective Office application")
                    .version("1.0.0")
            )
            .addSecurityItem(
                SecurityRequirement().addList("bearerAuth")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        io.swagger.v3.oas.models.security.SecurityScheme()
                            .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("Token")
                            .`in`(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                    )
            )
    }
}