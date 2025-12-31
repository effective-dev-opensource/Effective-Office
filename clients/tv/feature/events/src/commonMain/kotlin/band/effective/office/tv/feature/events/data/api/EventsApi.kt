package band.effective.office.tv.feature.events.data.api

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.events.data.dto.EventsResponseDTO

/**
 * API contract for Events feature.
 * Provides access to upcoming events from the backend.
 */
interface EventsApi {
    /**
     * Load upcoming events.
     *
     * @return Either containing list of events or error response
     */
    suspend fun getEvents(): Either<ErrorResponse, EventsResponseDTO>
}
