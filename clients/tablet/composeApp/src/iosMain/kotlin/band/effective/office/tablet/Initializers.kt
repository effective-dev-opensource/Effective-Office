package band.effective.office.tablet

import band.effective.office.tablet.di.KoinInitializer

class Initializers {

    fun init() {
        LoggerInitializer().init()
        KoinInitializer().init()
    }
}