package band.effective.office.tablet

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class LoggerInitializer {
    fun init() {
        if (isDebug) {
            Napier.base(DebugAntilog())
        }
    }
}