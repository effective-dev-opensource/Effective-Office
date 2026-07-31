package band.effective.office.tablet.core.domain.di

import band.effective.office.tablet.core.domain.model.SettingsStore
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * multiplatform-settings под linux не публикуется, поэтому настройки пока живут в памяти
 * процесса: выбранная переговорка не переживает перезапуск приложения.
 *
 * TODO: заменить на `ru.auroraos.kmp:ak-shared-preferences` — он есть в форке.
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
