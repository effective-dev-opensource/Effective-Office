package band.effective.office.tablet.feature.main.domain

import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime

/**
 * A singleton that holds the current time.
 */
object CurrentTimeHolder {
    private val _currentTime = MutableStateFlow(currentLocalDateTime)
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