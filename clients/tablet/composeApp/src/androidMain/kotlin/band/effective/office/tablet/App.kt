package band.effective.office.tablet

import android.app.Application
import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.di.KoinInitializer
import com.russhwolf.settings.SharedPreferencesSettings

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LoggerInitializer().init()
        KoinInitializer().init()
        SettingsManager.init(
            SharedPreferencesSettings(
                this.getSharedPreferences(
                    "settings",
                    MODE_PRIVATE
                )
            )
        )
    }
}