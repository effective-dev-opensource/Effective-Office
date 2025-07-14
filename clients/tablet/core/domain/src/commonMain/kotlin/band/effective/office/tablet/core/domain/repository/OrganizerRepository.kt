package band.effective.office.tablet.core.domain.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.Organizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**Repository for get info about organizers*/
interface OrganizerRepository {
    suspend fun getOrganizersList(): Either<ErrorWithData<List<Organizer>>, List<Organizer>>
    fun subscribeOnUpdates(scope: CoroutineScope): Flow<Either<ErrorWithData<List<Organizer>>, List<Organizer>>>
}