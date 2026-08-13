package band.effective.office.tablet.di

import band.effective.office.tablet.time.TimeReceiver
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun timeReceiverModule(): Module = module {
    single { TimeReceiver() }
}
