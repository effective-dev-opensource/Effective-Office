package band.effective.office.backend.feature.leader.id.api

import band.effective.office.backend.feature.leader.id.dto.LeaderIdEventInfoResponse
import band.effective.office.backend.feature.leader.id.dto.LeaderIdSearchEventsResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

/**
 * HTTP API interface for LeaderId service.
 * Uses Spring HTTP Interface (similar to Retrofit) for clean HTTP client implementation.
 */
interface LeaderIdApi {

    /**
     * Search for events based on criteria.
     *
     * @param paginationSize Number of events per page
     * @param dateFrom Start date for search (YYYY-MM-DD format)
     * @param dateTo End date for search (YYYY-MM-DD format)
     * @param cityId City ID to search in
     * @param placeIds Place IDs to search in (array parameter)
     * @return Response containing list of event IDs
     */
    @GetExchange("/api/v4/events/search")
    fun searchEvents(
        @RequestParam("paginationSize") paginationSize: Int,
        @RequestParam("dateFrom") dateFrom: String,
        @RequestParam("dateTo") dateTo: String,
        @RequestParam("cityId") cityId: Int,
        @RequestParam("placeIds[]") placeIds: Int
    ): LeaderIdSearchEventsResponse

    /**
     * Get detailed information about a specific event.
     *
     * @param eventId The ID of the event to retrieve
     * @return Response containing detailed event information
     */
    @GetExchange("/api/v4/events/{eventId}")
    fun getEventInfo(
        @PathVariable("eventId") eventId: Int
    ): LeaderIdEventInfoResponse
}

