package band.effective.office.tablet.core.data.api.impl

import band.effective.office.tablet.core.data.api.BookingApi
import band.effective.office.tablet.core.data.api.Collector
import band.effective.office.tablet.core.data.dto.SuccessResponse
import band.effective.office.tablet.core.data.dto.booking.BookingRequestDTO
import band.effective.office.tablet.core.data.dto.booking.BookingResponseDTO
import band.effective.office.tablet.core.data.network.HttpClientProvider
import band.effective.office.tablet.core.data.network.HttpRequestUtil
import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorResponse
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Implementation of the BookingApi interface
 */
class BookingApiImpl(
    private val collector: Collector<String>,
) : BookingApi {

    private val client = HttpClientProvider.create()

    override suspend fun getBooking(id: String): Either<ErrorResponse, BookingResponseDTO> {
        return when (val result = HttpRequestUtil.request<BookingResponseDTO>(
            client = client,
            url = "/api/v1/bookings",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                appendPathSegments(id)
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun getBookingsByUser(
        userId: String,
        beginDate: Long,
        endDate: Long
    ): Either<ErrorResponse, List<BookingResponseDTO>> {
        return when (val result = HttpRequestUtil.request<List<BookingResponseDTO>>(
            client = client,
            url = "/api/v1/bookings",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                parameters.append("user_id", userId)
                parameters.append("range_from", beginDate.toString())
                parameters.append("range_to", endDate.toString())
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun getBookingsByWorkspaces(
        workspaceId: String,
        from: Long?,
        to: Long?
    ): Either<ErrorResponse, List<BookingResponseDTO>> {
        return when (val result = HttpRequestUtil.request<List<BookingResponseDTO>>(
            client = client,
            url = "/api/v1/bookings",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                parameters.append("workspace_id", workspaceId)
                if (from != null) {
                    parameters.append("range_from", from.toString())
                }
                if (to != null) {
                    parameters.append("range_to", to.toString())
                }
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun createBooking(bookingInfo: BookingRequestDTO): Either<ErrorResponse, BookingResponseDTO> {
        return when (val result = HttpRequestUtil.request<BookingResponseDTO>(
            client = client,
            url = "/api/v1/bookings",
            method = HttpRequestUtil.Method.POST
        ) {
            contentType(ContentType.Application.Json)
            setBody(bookingInfo)
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun updateBooking(
        bookingInfo: BookingRequestDTO,
        bookingId: String
    ): Either<ErrorResponse, BookingResponseDTO> {
        return when (val result = HttpRequestUtil.request<BookingResponseDTO>(
            client = client,
            url = "/api/v1/bookings",
            method = HttpRequestUtil.Method.PUT
        ) {
            url {
                appendPathSegments(bookingId)
            }
            contentType(ContentType.Application.Json)
            Json.encodeToString(bookingInfo)
            setBody(bookingInfo)
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override suspend fun deleteBooking(bookingId: String): Either<ErrorResponse, SuccessResponse> {
        return when (val result = HttpRequestUtil.request<SuccessResponse>(
            client = client,
            url = "/api/v1/bookings",
            method = HttpRequestUtil.Method.DELETE
        ) {
            url {
                appendPathSegments(bookingId)
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(SuccessResponse(true))
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }

    override fun subscribeOnBookingsList(
        workspaceId: String,
    ): Flow<Either<ErrorResponse, List<BookingResponseDTO>>> {
        return collector.flow()
            .filter { it == "effectiveoffice-booking" }
            .map { Either.Success(listOf()) }
    }

    override suspend fun getBookings(
        rangeFrom: Long?,
        rangeTo: Long?
    ): Either<ErrorResponse, List<BookingResponseDTO>> {
        return when (val result = HttpRequestUtil.request<List<BookingResponseDTO>>(
            client = client,
            url = "/api/v1/bookings",
            method = HttpRequestUtil.Method.GET
        ) {
            url {
                if (rangeFrom != null) {
                    parameters.append("range_from", rangeFrom.toString())
                }
                if (rangeTo != null) {
                    parameters.append("range_to", rangeTo.toString())
                }
            }
        }) {
            is HttpRequestUtil.Result.Success -> Either.Success(result.data)
            is HttpRequestUtil.Result.Error -> Either.Error(ErrorResponse(result.code, result.message))
        }
    }
}