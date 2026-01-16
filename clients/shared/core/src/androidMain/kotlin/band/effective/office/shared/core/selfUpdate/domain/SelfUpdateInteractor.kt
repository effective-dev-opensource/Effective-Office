package band.effective.office.shared.core.selfUpdate.domain

import io.github.aakira.napier.Napier

class SelfUpdateInteractor(
    private val updateInfoRepository: UpdateInfoRepository,
    private val downloader: FileDownloader,
    private val installer: UpdateInstaller
) {
    suspend fun getUpdateInfo() = updateInfoRepository.getAndStoreUpdateInfo()

    suspend fun downloadAndInstallUpdate(): Boolean {
        val source = updateInfoRepository.getStoredUpdateInfo()?.url ?: return false
        val apk = downloader.loadFile(source)
            .onFailure { Napier.e(message = "Fail download APK", throwable = it) }
            .getOrNull() ?: return false
        installer.install(apk)
        return true
    }
}