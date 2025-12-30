package band.effective.office.smsrouter.domain

/**
 * Represents an error response.
 * @property code Error code
 * @property description Error description
 */
data class ErrorResponse(val code: Int, val description: String) {
    companion object {
        /**
         * Creates an error response based on the error code.
         * @param code Error code
         * @return ErrorResponse with a description based on the code
         */
        fun getResponse(code: Int): ErrorResponse {
            val description = when (code) {
                404 -> "Not found"
                in 400..499 -> "Client error"
                in 500..599 -> "Server error"
                else -> "Unknown error"
            }
            return ErrorResponse(code, description)
        }
    }
}