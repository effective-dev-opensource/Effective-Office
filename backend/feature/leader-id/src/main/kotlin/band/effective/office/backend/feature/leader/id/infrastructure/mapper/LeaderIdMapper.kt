package band.effective.office.backend.feature.leader.id.infrastructure.mapper

import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEvent
import band.effective.office.backend.feature.leader.id.domain.model.LeaderIdEventSearchCriteria
import band.effective.office.backend.feature.leader.id.dto.LeaderIdEventDTO
import band.effective.office.backend.feature.leader.id.dto.LeaderIdEventInfoResponse
import band.effective.office.backend.feature.leader.id.constants.LeaderIdApiConstants
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Mapper for converting between domain models and DTOs.
 * 
 * This mapper handles the conversion between the internal domain models
 * and the external API DTOs, ensuring proper data transformation a
 */
@Component
class LeaderIdMapper {

    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    fun toEventDTO(event: LeaderIdEvent): LeaderIdEventDTO {
        return LeaderIdEventDTO(
            id = event.id,
            name = event.name,
            startDateTime = event.startDateTime,
            finishDateTime = event.finishDateTime,
            isOnline = event.isOnline,
            photoUrl = event.photoUrl,
            organizer = event.organizer,
            speakers = event.speakers.takeIf { it.isNotEmpty() },
            endRegDate = event.endRegDate
        )
    }

    fun toDomainEvent(eventInfo: LeaderIdEventInfoResponse): LeaderIdEvent {
        val data = eventInfo.data
        return LeaderIdEvent(
            id = data.id,
            name = data.fullName,
            startDateTime = parseDateTime(data.dateStart),
            finishDateTime = parseDateTime(data.dateEnd),
            isOnline = data.status == LeaderIdApiConstants.ONLINE_STATUS,
            photoUrl = data.photo,
            organizer = data.organizers.firstOrNull()?.name,
            speakers = data.speakers.map { "${it.user.firstName} ${it.user.lastName}" },
            endRegDate = data.registrationDateEnd?.let { parseDateTime(it) }
        )
    }

    /**
     * Parses date-time string to LocalDateTime.
     */
    private fun parseDateTime(dateTimeString: String): LocalDateTime {
        return try {
            LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to parse date-time string: $dateTimeString", e)
        }
    }
}
