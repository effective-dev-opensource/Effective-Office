package band.effective.office.tablet

import band.effective.office.tablet.core.domain.model.SettingsStore
import io.github.aakira.napier.Napier
import ru.auroraos.kmp.sharedPreferences.SharedPreferences

private const val SETTINGS_TAG = "Settings"

/**
 * multiplatform-settings publishes no linux target, so the fork's own shared preferences back the
 * store here. A failed setting is logged and survived, not fatal.
 */
internal class AuroraSettingsStore : SettingsStore {

    override fun getString(key: String, defaultValue: String): String =
        runCatching { SharedPreferences.getString(key, defaultValue) }
            .getOrElse {
                Napier.e(throwable = it, tag = SETTINGS_TAG) { "reading '$key' failed" }
                defaultValue
            }

    override fun putString(key: String, value: String) = write(key) {
        SharedPreferences.putString(key, value)
    }

    override fun remove(key: String) = write(key) {
        SharedPreferences.remove(key)
    }

    // A put only reaches the process; save() is what reaches the disk, and the wall-mounted
    // tablet reboots.
    private inline fun write(key: String, block: () -> Unit) {
        runCatching {
            block()
            SharedPreferences.save()
        }.onFailure {
            Napier.e(throwable = it, tag = SETTINGS_TAG) { "writing '$key' failed" }
        }
    }
}
