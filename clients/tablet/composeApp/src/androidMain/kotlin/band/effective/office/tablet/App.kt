package band.effective.office.tablet

import android.app.Application
import band.effective.office.tablet.di.KoinInitializer

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LoggerInitializer().init()
        KoinInitializer().init()
    }
}