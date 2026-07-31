package band.effective.office.tablet.core.domain.di

import band.effective.office.tablet.core.domain.model.SettingsStore
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * multiplatform-settings publishes no linux target, so settings live in process memory for
 * now: the selected meeting room does not survive a restart.
 *
 * TODO: replace with `ru.auroraos.kmp:ak-shared-preferences`, which the fork ships.
 */
actual fun settingsStoreModule(): Module = module {
    single<SettingsStore> {
        object : SettingsStore {
            private val values = mutableMapOf<String, String>()

            override fun getString(key: String, defaultValue: String): String =
                values[key] ?: defaultValue

            override fun putString(key: String, value: String) {
                values[key] = value
            }

            override fun remove(key: String) {
                values.remove(key)
            }
        }
    }
}
