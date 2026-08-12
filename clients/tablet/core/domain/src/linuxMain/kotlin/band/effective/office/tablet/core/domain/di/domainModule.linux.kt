package band.effective.office.tablet.core.domain.di

import band.effective.office.tablet.core.domain.model.SettingsStore
import io.github.aakira.napier.Napier
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.auroraos.kmp.sharedPreferences.SharedPreferences

/** Same log tag family as the rest of the Aurora diagnostics. */
private const val SETTINGS_TAG = "Settings"

/**
 * multiplatform-settings publishes no linux target, so the store is backed by the fork's own
 * `ru.auroraos.kmp:ak-shared-preferences` instead. What it buys is the point of the thing: the
 * meeting room picked on first run is still picked after a restart, which on a wall-mounted
 * tablet is the difference between being set up once and being set up every time it reboots.
 */
actual fun settingsStoreModule(): Module = module {
    single<SettingsStore> { AuroraSettingsStore() }
}

private class AuroraSettingsStore : SettingsStore {

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

    /**
     * Every write is followed by [SharedPreferences.save], which is what actually reaches the
     * disk — without it the value lives in the process and is lost with it, which is the very
     * thing the in-memory map this replaced was criticised for. Saving per write rather than on
     * some later occasion is affordable here: the only setting is the room, and it is written
     * when somebody picks one.
     *
     * Wrapped like every other call into the fork: a setting that cannot be stored is not worth
     * an app. It is logged and the caller carries on with an answer that is merely not persisted.
     */
    private inline fun write(key: String, block: () -> Unit) {
        runCatching {
            block()
            SharedPreferences.save()
        }.onFailure {
            Napier.e(throwable = it, tag = SETTINGS_TAG) { "writing '$key' failed" }
        }
    }
}
