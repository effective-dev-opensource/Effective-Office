package band.effective.office.smsrouter

import android.app.Application
import band.effective.office.smsrouter.data.di.dataModule
import band.effective.office.smsrouter.domain.di.domainModule
import band.effective.office.smsrouter.presentation.di.appModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

class SmsRouterApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }

        startKoin {
            modules(
                appModule,
                dataModule,
                domainModule,
            )
        }
    }
}