package band.effective.office.client.core.data.model

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Error] or [Success].
 */
sealed interface Either<out ErrorType, out DataType> {
    /**
     * Represents the error case of the disjoint union.
     */
    data class Error<out ErrorType>(val error: ErrorType) : Either<ErrorType, Nothing>

    /**
     * Represents the success case of the disjoint union.
     */
    data class Success<out DataType>(val data: DataType) : Either<Nothing, DataType>
}