package band.effective.office.tv.feature.events.data.api.impl

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.core.data.network.get
import band.effective.office.tv.feature.events.data.api.EventsApi
import band.effective.office.tv.feature.events.data.dto.EventsResponseDTO
import io.ktor.client.HttpClient

/**
 * Implementation of [EventsApi] using shared HTTP stack.
 */
class EventsApiImpl(private val client: HttpClient) : EventsApi {

    override suspend fun getEvents(): Either<ErrorResponse, EventsResponseDTO> =
        get(client, "api/v1/events")
}
