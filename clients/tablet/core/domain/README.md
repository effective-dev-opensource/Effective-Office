# Domain Module

## Overview
The Domain module is the core business logic layer of the Effective Office tablet application. It contains use cases, domain models, and repository interfaces that define the business rules of the application. This module is independent of any framework or platform-specific code, making it highly testable and reusable.

## Features
- Business logic implementation through use cases
- Domain model definitions
- Repository interfaces
- Business rule validation
- Platform-independent code

## Architecture
The module follows a clean architecture approach:

```
domain/
├── model/           # Domain models
├── repository/      # Repository interfaces
├── usecase/         # Use cases implementing business logic
├── exception/       # Domain-specific exceptions
└── util/            # Utility classes and extensions
```

## Key Components

### Models
- **Domain Models**: Business entities that represent the core concepts of the application
- **Value Objects**: Immutable objects representing values with no identity

### Repositories
- **Repository Interfaces**: Contracts that define how to access and manipulate data
- **Data Source Abstractions**: Interfaces for different data sources

### Use Cases
- **Interactors**: Implementations of specific business operations

### Error Handling
- **Either Type**: A functional approach to handling success and error cases

## Integration
The Domain module is used by:
- Feature modules that implement specific application features
- Data module that implements the repository interfaces

## Development
### Adding a New Use Case
To add a new use case:
1. Define the input and output models if needed
2. Create a new use case class in the appropriate package
3. Implement the business logic
4. Write unit tests for the use case

### Testing
The module is designed to be highly testable:
- Unit tests for use cases with mock repositories
- No dependencies on external frameworks or libraries

## Using the Either Type for Error Handling

### Overview
The `Either` type is a functional programming construct used for representing a value that can be one of two possible types: a success value or an error value. In our application, it's used to handle operation results in a type-safe manner, eliminating the need for exceptions or null checks.

### Structure
```kotlin
sealed interface Either<out ErrorType, out DataType> {
    data class Error<out ErrorType>(val error: ErrorType) : Either<ErrorType, Nothing>
    data class Success<out DataType>(val data: DataType) : Either<Nothing, DataType>
}
```

### Extension Functions
The `Either` type comes with several extension functions to make it easier to work with:

1. **unbox**: Extracts the value from Either, handling both success and error cases
   ```kotlin
   fun <ErrorType, DataType> Either<ErrorType, DataType>.unbox(
       errorHandler: (ErrorType) -> DataType,
       successHandler: ((DataType) -> DataType)? = null
   ): DataType
   ```

2. **map**: Transforms both the error and success values of an Either into new types
   ```kotlin
   fun <OldErrorType, oldDataType, ErrorType, DataType> Either<OldErrorType, oldDataType>.map(
       errorMapper: (OldErrorType) -> ErrorType,
       successMapper: (oldDataType) -> DataType,
   ): Either<ErrorType, DataType>
   ```

3. **asyncMap**: A suspend version of map for asynchronous transformations
   ```kotlin
   suspend fun <OldErrorType, oldDataType, ErrorType, DataType> Either<OldErrorType, oldDataType>.asyncMap(
       errorMapper: suspend (OldErrorType) -> ErrorType,
       successMapper: suspend (oldDataType) -> DataType,
   ): Either<ErrorType, DataType>
   ```

### Usage Examples

#### 1. In Remote Data Source
Remote data sources return `Either` to handle network errors:

```kotlin
interface BookingApi {
    suspend fun getBooking(id: String): Either<ErrorResponse, BookingResponseDTO>
    // Other methods...
}

class BookingApiImpl(private val httpClient: HttpClient) : BookingApi {
    override suspend fun getBooking(id: String): Either<ErrorResponse, BookingResponseDTO> {
        return try {
            val response = httpClient.get("bookings/$id")
            Either.Success(response.body())
        } catch (e: Exception) {
            Either.Error(ErrorResponse(e.message ?: "Unknown error"))
        }
    }
}
```

#### 2. In Repository Layer
Repositories use `map` to transform DTOs to domain models:

```kotlin
class BookingRepositoryImpl(
    private val api: BookingApi,
    private val mapper: EventInfoMapper,
) : BookingRepository {
    override suspend fun getBooking(eventInfo: EventInfo): Either<ErrorResponse, EventInfo> {
        val response = api.getBooking(eventInfo.id)
        return response.map(
            errorMapper = { it }, // Pass through the error
            successMapper = mapper::map, // Transform DTO to domain model
        )
    }
}
```

#### 3. Processing Results in Presentation Layer
Handle both success and error cases when processing results:

```kotlin
private fun processRoomInfoResult(result: Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>): RoomsResult {
    return when (result) {
        is Either.Error -> RoomsResult(
            isSuccess = false,
            roomList = result.error.saveData ?: listOf(RoomInfo.defaultValue),
            indexSelectRoom = 0
        )
        is Either.Success -> RoomsResult(
            isSuccess = true,
            roomList = result.data,
            indexSelectRoom = calculateRoomIndex(result.data)
        )
    }
}
```

#### 4. Using unbox to Extract Values
Extract values with fallback handling:

```kotlin
val rooms = roomInfoResult.unbox(
    errorHandler = { error -> error.saveData ?: emptyList() },
    successHandler = { data -> data.filterAvailable() }
)
```

### Best Practices
1. Always handle both success and error cases
2. Use `map` to transform data between layers
3. Provide meaningful error types that contain enough information for error handling
4. Consider including fallback data in error types for graceful degradation
5. Use `unbox` when you need to extract the final value, typically in the presentation layer
