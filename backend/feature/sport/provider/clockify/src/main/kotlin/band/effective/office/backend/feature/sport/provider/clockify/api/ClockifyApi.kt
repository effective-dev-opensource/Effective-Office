package band.effective.office.backend.feature.sport.provider.clockify.api

import band.effective.office.backend.feature.sport.provider.clockify.model.ClockifyRequest
import band.effective.office.backend.feature.sport.provider.clockify.model.ClockifyResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.PostExchange

interface ClockifyApi {

    @PostExchange("/v1/workspaces/{workspaceId}/reports/detailed")
    fun getDetailedReports(
        @PathVariable workspaceId: String,
        @RequestHeader("x-api-key") apiKey: String,
        @RequestBody request: ClockifyRequest
    ): ClockifyResponse
}