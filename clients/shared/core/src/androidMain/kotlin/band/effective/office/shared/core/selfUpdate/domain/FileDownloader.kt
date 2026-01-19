package band.effective.office.shared.core.selfUpdate.domain

interface FileDownloader {
    suspend fun loadFile(source: String): Result<String>
}