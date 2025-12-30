package band.effective.office.shared.core.network.dto

import kotlinx.serialization.Serializable

/**
 * Represents a successful response from the server.
 * Used for endpoints that return 204 No Content or 201 Created without a body.
 * @property success Indicates whether the operation was successful
 */
@Serializable
data class SuccessResponse(val success: Boolean = true)
