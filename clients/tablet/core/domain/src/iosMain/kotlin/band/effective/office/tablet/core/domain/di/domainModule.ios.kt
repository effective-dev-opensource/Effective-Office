package band.effective.office.tablet.core.domain.di

import band.effective.office.tablet.core.domain.model.SettingsStore
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import org.koin.dsl.module

@OptIn(ExperimentalSettingsImplementation::class)
actual fun settingsStoreModule() = module {
    single<SettingsStore> {
        val settings = KeychainSettings("effectiveOffice")
        object : SettingsStore {
            override fun getString(key: String, defaultValue: String) = settings.getString(key, defaultValue)
            override fun putString(key: String, value: String) = settings.putString(key, value)
            override fun remove(key: String) = settings.remove(key)
        }
    }
}
