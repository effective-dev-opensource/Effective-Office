package band.effective.office.tablet.core.data.dto.user

import kotlinx.serialization.Serializable

/**
 * Represents an integration with an external service.
 * @property id Unique identifier
 * @property name Name of the integration
 * @property value Value of the integration
 */
@Serializable
data class IntegrationDTO(
    val id: String,
    val name: String,
    val value: String
)