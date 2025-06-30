package band.effective.office.tablet.core.data.dto

import kotlinx.serialization.Serializable

/**
 * Represents a successful response from the server.
 * @property success Indicates whether the operation was successful
 */
@Serializable
data class SuccessResponse(val success: Boolean = true)