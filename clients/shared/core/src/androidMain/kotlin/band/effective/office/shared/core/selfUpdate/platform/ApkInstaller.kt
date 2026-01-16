package band.effective.office.shared.core.selfUpdate.platform

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import band.effective.office.shared.core.selfUpdate.domain.UpdateInstaller
import io.github.aakira.napier.Napier
import java.io.File

class ApkInstaller(
    private val context: Context
) : UpdateInstaller {
    override fun install(path: String) = runCatching {
        Napier.i { "start install update" }
        val intent = Intent(Intent.ACTION_VIEW)
        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File(path)
        )
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        Napier.i { "start activity for update" }
        context.startActivity(intent)
    }.onFailure {
        Napier.e(message = "Fail update app", throwable = it)
    }
}