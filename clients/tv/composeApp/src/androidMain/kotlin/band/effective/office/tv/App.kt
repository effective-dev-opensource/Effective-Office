package band.effective.office.tv

import android.app.Application
import band.effective.office.tv.di.KoinInitializer
import band.effective.office.tv.environment.Environment
import band.effective.office.tv.logger.LoggerInitializer

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val environment = Environment.fromBuildKonfig()
        LoggerInitializer().init(environment.isDebug)
        KoinInitializer().init(environment)
    }
}

