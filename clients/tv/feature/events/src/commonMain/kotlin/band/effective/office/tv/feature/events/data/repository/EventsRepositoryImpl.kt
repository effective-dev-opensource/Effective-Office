package band.effective.office.tv.feature.events.data.repository

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.shared.core.domain.asFlow
import band.effective.office.tv.feature.events.data.api.EventsApi
import band.effective.office.tv.feature.events.data.mapper.EventMapper
import band.effective.office.tv.feature.events.domain.model.EventInfo
import band.effective.office.tv.feature.events.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class EventsRepositoryImpl(
    private val api: EventsApi,
) : EventsRepository {
    override suspend fun getEvents(): Flow<Either<ErrorResponse, EventInfo>> {
        return when (val result = api.getEvents()) {
            is Either.Success -> {
                val events = result.data.events.mapNotNull { EventMapper.toDomain(it) }
                Either.Success(events).asFlow()
            }
            is Either.Error -> flowOf(Either.Error(result.error))
        }
    }
}
