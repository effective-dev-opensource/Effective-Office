package band.effective.office.shared.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

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

/**
 * Handles Either result with callbacks for success and error cases.
 * Similar to Result.fold, both callbacks return the same type T.
 * @param onSuccess Callback for success case that transforms DataType to T.
 * @param onError Callback for error case that transforms ErrorType to T.
 * @return Result of either onSuccess or onError callback.
 */
inline fun <ErrorType, DataType, T> Either<ErrorType, DataType>.fold(
    onSuccess: (DataType) -> T,
    onError: (ErrorType) -> T
): T {
    return when (this) {
        is Either.Success -> onSuccess(this.data)
        is Either.Error -> onError(this.error)
    }
}

/**
 * Converts Either<ErrorResponse, Iterable<T>> to Flow<Either<ErrorResponse, T>>.
 * @return Flow emitting each element of the iterable wrapped in Either.Success, or the original Either.Error.
 */
fun <T> Either<ErrorResponse, Iterable<T>>.asFlow(): Flow<Either<ErrorResponse, T>> =
    when (this) {
        is Either.Error -> flowOf<Either<ErrorResponse, T>>(this)
        is Either.Success -> flow {
            data.forEach { emit(Either.Success(it)) }
        }
    }

/**
 * Collects an element-wise flow of Either<ErrorResponse, T> into a single Either<ErrorResponse, List<T>>.
 * 
 * WARNING: This function will collect all elements from the flow until it completes.
 * Do NOT use on infinite flows like StateFlow, as it will never complete.
 * Use only on finite flows (e.g., flows created with flow { ... } that emit and complete).
 *
 * @return Either.Success with the full list on success, or the first encountered Either.Error.
 */
suspend fun <T> Flow<Either<ErrorResponse, T>>.collectToEitherList(): Either<ErrorResponse, List<T>> {
    val list = mutableListOf<T>()
    var firstError: Either.Error<ErrorResponse>? = null

    collect { either ->
        when (either) {
            is Either.Error -> {
                firstError = either
                return@collect
            }
            is Either.Success -> list.add(either.data)
        }
    }

    return firstError ?: Either.Success(list)
}
