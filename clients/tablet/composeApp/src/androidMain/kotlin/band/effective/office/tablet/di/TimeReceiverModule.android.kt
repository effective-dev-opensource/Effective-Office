package band.effective.office.tablet.di

import android.content.Context
import band.effective.office.tablet.time.TimeReceiver
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun timeReceiverModule(): Module = module {
    single { TimeReceiver(context = get<Context>(), currentTimeHolder = get()) }
}
