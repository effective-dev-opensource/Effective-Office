package band.effective.office.tablet.core.domain.util

interface ComponentLoggable : Loggable {
    fun <I> sendIntent(intent: I)
}