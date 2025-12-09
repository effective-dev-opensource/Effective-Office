package band.effective.office.shared.core.domain

/**
 * Extension functions for working with Either type
 */

/**
 * Unwraps Either type and handles both success and error cases
 * @param errorHandler Function to handle error case
 * @param successHandler Optional function to transform success case
 * @return Result of error or success handler
 */
fun <ErrorType, DataType> Either<ErrorType, DataType>.unbox(
    errorHandler: (ErrorType) -> DataType,
    successHandler: ((DataType) -> DataType)? = null
): DataType =
    when (this) {
        is Either.Error -> errorHandler(this.error)
        is Either.Success -> successHandler?.invoke(data) ?: data
    }

/**
 * Maps both error and success values to new types
 * @param errorMapper Function to map error type
 * @param successMapper Function to map success type
 * @return New Either with mapped values
 */
fun <OldErrorType, OldDataType, ErrorType, DataType> Either<OldErrorType, OldDataType>.map(
    errorMapper: (OldErrorType) -> ErrorType,
    successMapper: (OldDataType) -> DataType,
): Either<ErrorType, DataType> =
    when (this) {
        is Either.Error -> Either.Error(errorMapper(this.error))
        is Either.Success -> Either.Success(successMapper(this.data))
    }

/**
 * Asynchronously maps both error and success values to new types
 * @param errorMapper Suspend function to map error type
 * @param successMapper Suspend function to map success type
 * @return New Either with mapped values
 */
suspend fun <OldErrorType, OldDataType, ErrorType, DataType> Either<OldErrorType, OldDataType>.asyncMap(
    errorMapper: suspend (OldErrorType) -> ErrorType,
    successMapper: suspend (OldDataType) -> DataType,
): Either<ErrorType, DataType> =
    when (this) {
        is Either.Error -> Either.Error(errorMapper(this.error))
        is Either.Success -> Either.Success(successMapper(data))
    }

/**
 * Returns the data if Either is Success, otherwise returns null
 */
fun <ErrorType, DataType> Either<ErrorType, DataType>.getOrNull(): DataType? =
    when (this) {
        is Either.Error -> null
        is Either.Success -> data
    }

/**
 * Returns the data if Either is Success, otherwise returns default value
 */
fun <ErrorType, DataType> Either<ErrorType, DataType>.getOrDefault(default: DataType): DataType =
    when (this) {
        is Either.Error -> default
        is Either.Success -> data
    }

/**
 * Returns true if Either is Success
 */
fun <ErrorType, DataType> Either<ErrorType, DataType>.isSuccess(): Boolean =
    this is Either.Success

/**
 * Returns true if Either is Error
 */
fun <ErrorType, DataType> Either<ErrorType, DataType>.isError(): Boolean =
    this is Either.Error
