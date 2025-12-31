package band.effective.office.tv.feature.events.domain.repository

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.events.domain.model.EventInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for Events feature.
 */
interface EventsRepository {
    /**
     * Load events from backend.
     */
    suspend fun getEvents(): Flow<Either<ErrorResponse, EventInfo>>
}
