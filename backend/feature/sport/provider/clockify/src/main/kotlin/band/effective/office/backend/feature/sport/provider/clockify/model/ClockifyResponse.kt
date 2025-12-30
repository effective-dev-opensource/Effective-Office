package band.effective.office.backend.feature.sport.provider.clockify.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Response model for Clockify API detailed reports.
 */
data class ClockifyResponse(
    @JsonProperty("timeentries")
    val timeEntries: List<ClockifyTimeEntry>?
)

/**
 * Time entry from Clockify API.
 */
data class ClockifyTimeEntry(
    @JsonProperty("userEmail")
    val userEmail: String,
    
    @JsonProperty("userName")
    val userName: String,
    
    @JsonProperty("timeInterval")
    val timeInterval: ClockifyTimeInterval?
)

/**
 * Time interval from Clockify API.
 */
data class ClockifyTimeInterval(
    @JsonProperty("duration")
    val duration: Int?
)