package band.effective.office.shared.core.selfUpdate.data

import band.effective.office.shared.core.selfUpdate.domain.UpdateInfo
import band.effective.office.shared.core.selfUpdate.domain.UpdateInfoRepository
import io.github.aakira.napier.Napier

class UpdateInfoRepositoryImpl(private val api: UpdateApi): UpdateInfoRepository {

    var cash: UpdateInfo? = null

    override suspend fun getAndStoreUpdateInfo(): UpdateInfo? {
        val fetchedInfo = api.getUpdateInfo()
            .onFailure { Napier.e { it.stackTraceToString() } }
            .onSuccess { cash = it }
            .getOrNull()
        return fetchedInfo
    }

    override suspend fun getStoredUpdateInfo(): UpdateInfo? = cash

}