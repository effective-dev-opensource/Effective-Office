package band.effective.office.tablet

import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.core.domain.model.SettingsStore
import band.effective.office.tablet.di.KoinInitializer
import com.russhwolf.settings.KeychainSettings

class Initializers {

    fun init() {
        LoggerInitializer().init()
        KoinInitializer().init()
        val settings = KeychainSettings("effectiveOffice")
        SettingsManager.init(object : SettingsStore {
            override fun getString(key: String, defaultValue: String) = settings.getString(key, defaultValue)
            override fun putString(key: String, value: String) = settings.putString(key, value)
            override fun remove(key: String) = settings.remove(key)
        })
    }
}