package band.effective.office.tablet.feature.main.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * A singleton that holds the current time.
 */
object CurrentTimeHolder {
    private val defaultTimeZone = TimeZone.Companion.currentSystemDefault()

    private val _currentTime = MutableStateFlow(Clock.System.now().toLocalDateTime(defaultTimeZone))
    val currentTime: StateFlow<LocalDateTime> = _currentTime.asStateFlow()

    /**
     * Updates the current time.
     */
    fun updateTime(time: LocalDateTime) {
        _currentTime.value = time
    }

    /**
     * Gets the current time.
     */
    fun getCurrentTime(): LocalDateTime {
        return _currentTime.value
    }
}