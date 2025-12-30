package band.effective.office.backend.feature.sport.provider.clockify.mapper

import band.effective.office.backend.feature.sport.core.domain.model.SportUser
import band.effective.office.backend.feature.sport.core.exception.SportUsersRetrievalFailedException
import band.effective.office.backend.feature.sport.provider.clockify.model.ClockifyResponse

/**
 * Mapper for converting Clockify API responses to domain models.
 */
object ClockifySportMapper {

    /**
     * Converts Clockify response to a list of SportUser domain models.
     * Groups time entries by user email and calculates total time spent.
     */
    fun toSportUsers(clockifyResponse: ClockifyResponse): List<SportUser> {
        val timeEntries = clockifyResponse.timeEntries ?: emptyList()
        
        if (timeEntries.isEmpty()) {
            throw SportUsersRetrievalFailedException("No time entries received from Clockify")
        }
        
        return timeEntries
            .filter { it.userEmail.isNotBlank() && it.userName.isNotBlank() }
            .groupBy { it.userEmail }
            .map { (email, timeEntries) ->
                SportUser(
                    name = timeEntries.first().userName,
                    email = email,
                    totalSeconds = timeEntries.sumOf { it.timeInterval?.duration ?: 0 }
                )
            }
    }
}