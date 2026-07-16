package band.effective.office.tablet.core.domain.di

import android.content.Context
import band.effective.office.tablet.core.domain.model.SettingsStore
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

actual fun settingsStoreModule() = module {
    single<SettingsStore> {
        val settings = SharedPreferencesSettings(
            get<Context>().getSharedPreferences("settings", Context.MODE_PRIVATE)
        )
        object : SettingsStore {
            override fun getString(key: String, defaultValue: String) = settings.getString(key, defaultValue)
            override fun putString(key: String, value: String) = settings.putString(key, value)
            override fun remove(key: String) = settings.remove(key)
        }
    }
}
