package band.effective.office.backend.feature.sport.provider.clockify.service

import band.effective.office.backend.feature.sport.core.domain.model.SportUser
import band.effective.office.backend.feature.sport.core.exception.SportUsersRetrievalFailedException
import band.effective.office.backend.feature.sport.provider.clockify.api.ClockifyApi
import band.effective.office.backend.feature.sport.provider.clockify.config.ClockifyCredentials
import band.effective.office.backend.feature.sport.provider.clockify.constants.ClockifyConstants
import band.effective.office.backend.feature.sport.provider.clockify.mapper.ClockifySportMapper
import band.effective.office.backend.feature.sport.provider.clockify.model.ClockifyRequest
import band.effective.office.backend.feature.sport.provider.clockify.model.DetailedFilter
import band.effective.office.backend.feature.sport.provider.clockify.model.Projects
import band.effective.office.backend.feature.sport.provider.clockify.util.QuarterDateRangeCalculator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for retrieving sport time tracking data from Clockify API.
 * Handles API communication and request building.
 */
@Service
class ClockifySportService(
    private val clockifyApi: ClockifyApi,
    private val clockifyCredentials: ClockifyCredentials
) {
    private val logger = LoggerFactory.getLogger(ClockifySportService::class.java)

    /**
     * Retrieves sport users with their time tracking data for the current quarter.
     */
    fun getSportUsers(): List<SportUser> = runCatching {
        logger.debug("Retrieving sport users from Clockify for current quarter")
        
        val request = buildCurrentQuarterRequest()
        val response = clockifyApi.getDetailedReports(
            workspaceId = clockifyCredentials.workspaceId,
            apiKey = clockifyCredentials.apiKey,
            request = request
        )
        
        ClockifySportMapper.toSportUsers(response)
    }.getOrElse { exception ->
        logger.error("Failed to retrieve sport users from Clockify: ${exception.message}", exception)
        throw SportUsersRetrievalFailedException("Failed to retrieve sport users from Clockify: ${exception.message}")
    }

    /**
     * Builds Clockify API request for current quarter sport time tracking.
     */
    private fun buildCurrentQuarterRequest(): ClockifyRequest {
        val dateRange = QuarterDateRangeCalculator.getCurrentQuarterDateRange()
        
        return ClockifyRequest(
            amountShown = ClockifyConstants.AMOUNT_SHOWN,
            dateRangeStart = dateRange.first,
            dateRangeEnd = dateRange.second,
            exportType = ClockifyConstants.EXPORT_TYPE,
            rounding = false,
            detailedFilter = DetailedFilter(
                sortColumn = ClockifyConstants.SORT_COLUMN,
                pageSize = ClockifyConstants.PAGE_SIZE
            ),
            projects = Projects(
                ids = listOf(clockifyCredentials.projectId)
            )
        )
    }
}