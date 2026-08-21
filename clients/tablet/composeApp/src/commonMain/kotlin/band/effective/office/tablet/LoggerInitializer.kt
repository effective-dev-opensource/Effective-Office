package band.effective.office.tablet

import band.effective.office.tablet.platform.isDebug
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class LoggerInitializer {
    fun init() {
        if (isDebug) {
            Napier.base(DebugAntilog())
        }
    }
}