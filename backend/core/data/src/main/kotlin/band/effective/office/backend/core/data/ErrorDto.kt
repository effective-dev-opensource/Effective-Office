package band.effective.office.backend.core.data

/**
 * DTO for error response.
 * This class represents the standardized error response format for all services.
 *
 * @property message Detailed description of the error
 * @property code Error code (not HTTP status code)
 */
data class ErrorDto(
    val message: String,
    val code: Int
)