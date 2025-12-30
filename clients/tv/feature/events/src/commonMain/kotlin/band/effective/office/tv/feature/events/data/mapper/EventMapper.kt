package band.effective.office.tv.feature.events.data.mapper

import band.effective.office.shared.core.utils.defaultTimeZone
import band.effective.office.tv.feature.events.data.dto.EventDTO
import band.effective.office.tv.feature.events.domain.model.EventInfo
import io.github.aakira.napier.Napier
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

internal object EventMapper {

    fun toDomain(dto: EventDTO): EventInfo? {
        val start = dto.startDateTime.parseDateTime()
        if (start == null) {
            Napier.w("Failed to parse startDateTime for event ${dto.id}: ${dto.startDateTime}")
            return null
        }
        val finish = dto.finishDateTime.parseDateTime()
        if (finish == null) {
            Napier.w("Failed to parse finishDateTime for event ${dto.id}: ${dto.finishDateTime}")
            return null
        }

        return EventInfo(
            id = dto.id,
            name = dto.name,
            startDateTime = start,
            finishDateTime = finish,
            isOnline = dto.isOnline,
            photoUrl = dto.photoUrl,
            organizer = dto.organizer,
            speakers = dto.speakers.orEmpty(),
            endRegDate = dto.endRegDate.parseDateTime(),
            location = dto.location
        )
    }

    private fun String?.parseDateTime(): LocalDateTime? {
        if (this.isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(this) }
            .getOrElse {
                runCatching { Instant.parse(this).toLocalDateTime(defaultTimeZone) }.getOrNull()
            }
    }
}
