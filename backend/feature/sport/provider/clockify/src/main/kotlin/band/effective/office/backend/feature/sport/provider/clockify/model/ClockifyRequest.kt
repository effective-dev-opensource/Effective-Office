package band.effective.office.backend.feature.sport.provider.clockify.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request model for Clockify API detailed reports.
 */
data class ClockifyRequest(
    @JsonProperty("amountShown")
    val amountShown: String,
    
    @JsonProperty("dateRangeEnd")
    val dateRangeEnd: String,
    
    @JsonProperty("dateRangeStart")
    val dateRangeStart: String,
    
    @JsonProperty("detailedFilter")
    val detailedFilter: DetailedFilter,
    
    @JsonProperty("exportType")
    val exportType: String,
    
    @JsonProperty("projects")
    val projects: Projects,
    
    @JsonProperty("rounding")
    val rounding: Boolean
)

/**
 * Detailed filter for Clockify request.
 */
data class DetailedFilter(
    @JsonProperty("sortColumn")
    val sortColumn: String,
    
    @JsonProperty("pageSize")
    val pageSize: Int
)

/**
 * Projects filter for Clockify request.
 */
data class Projects(
    @JsonProperty("ids")
    val ids: List<String>
)