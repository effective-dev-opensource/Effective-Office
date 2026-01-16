package band.effective.office.shared.core.selfUpdate.platform

import band.effective.office.shared.core.selfUpdate.domain.FileDownloader
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import java.io.File

class FileDownloaderImpl(
    private val client: HttpClient
) : FileDownloader {
    override suspend fun loadFile(source: String): Result<String> = runCatching {
        val apkFile = File.createTempFile("installer", "apk")
        val bytes = client.get(source).readRawBytes()
        apkFile.outputStream().use { it.write(bytes) }
        apkFile.path
    }
}