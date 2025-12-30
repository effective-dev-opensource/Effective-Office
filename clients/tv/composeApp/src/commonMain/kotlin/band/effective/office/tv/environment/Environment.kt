package band.effective.office.tv.environment

import band.effective.office.tv.BuildKonfig
import band.effective.office.tv.utils.isDebug

data class Environment(
    val apiUrl: String,
    val apiKey: String,
    val versionName: String,
    val isDebug: Boolean,
) {
    companion object {
        fun fromBuildKonfig(): Environment {
            val debug = isDebug
            val url = if (debug) BuildKonfig.API_URL_DEBUG else BuildKonfig.API_URL_RELEASE
            return Environment(
                apiUrl = url,
                apiKey = BuildKonfig.API_KEY,
                versionName = BuildKonfig.VERSION_NAME,
                isDebug = debug,
            )
        }
    }
}

