package band.effective.office.tablet

import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.di.KoinInitializer
import com.russhwolf.settings.KeychainSettings

class Initializers {

    fun init() {
        LoggerInitializer().init()
        KoinInitializer().init()
        SettingsManager.init(KeychainSettings("effectiveOffice"))
    }
}