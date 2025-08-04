package band.effective.office.backend.feature.notifications.controller

import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.springframework.stereotype.Component

@Component
class NotificationDeduplicator {

    private val ttlSeconds = 5L

    @OptIn(ExperimentalTime::class)
    private val seenEvents = ConcurrentHashMap<String, Instant>()

    @OptIn(ExperimentalTime::class)
    fun isDuplicate(eventId: String): Boolean {
        val now = Clock.System.now()

        // Очистка устаревших
        seenEvents.entries.removeIf { (_, timestamp) ->
            timestamp + ttlSeconds.seconds < now
        }

        // Добавим, если ещё нет
        return seenEvents.putIfAbsent(eventId, now) != null
    }
}