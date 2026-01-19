package band.effective.office.shared.core.selfUpdate.domain

interface UpdateInfoRepository {
    suspend fun getAndStoreUpdateInfo(): UpdateInfo?
    suspend fun getStoredUpdateInfo(): UpdateInfo?
}