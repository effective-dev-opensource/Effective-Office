package band.effective.office.tablet.feature.main.domain

import band.effective.office.shared.core.utils.defaultTimeZone
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import kotlinx.datetime.TimeZone

/**
 * Re-reads the cache when the system zone changes: cached events carry wall-clock times computed in
 * the zone of the response, so afterwards they are read back an offset away and a booking under way
 * looks past. Checked on the tick, so a platform with no zone-change signal catches up in a minute.
 */
class RefreshOnTimeZoneChangeUseCase(
    private val roomInfoUseCase: RoomInfoUseCase,
) {
    private var knownTimeZone: TimeZone = defaultTimeZone

    suspend operator fun invoke() {
        CurrentTimeHolder.currentTime.collect {
            val currentTimeZone = defaultTimeZone
            if (currentTimeZone == knownTimeZone) return@collect
            knownTimeZone = currentTimeZone
            roomInfoUseCase.updateCache()
        }
    }
}
