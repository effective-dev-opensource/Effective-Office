package band.effective.office.tv

import android.app.Application
import band.effective.office.shared.core.crashlytics.OfficeUncaughtExceptionHandler
import band.effective.office.tv.di.KoinInitializer
import band.effective.office.tv.environment.Environment
import band.effective.office.tv.logger.LoggerInitializer
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val environment = Environment.fromBuildKonfig()
        LoggerInitializer().init(environment.isDebug)
        KoinInitializer().init(environment) {
            androidContext(this@App)
        }
        val officeUncaughtExceptionHandler: OfficeUncaughtExceptionHandler by inject {
            parametersOf(Thread.getDefaultUncaughtExceptionHandler())
        }
        Thread.setDefaultUncaughtExceptionHandler(officeUncaughtExceptionHandler)
    }
}

