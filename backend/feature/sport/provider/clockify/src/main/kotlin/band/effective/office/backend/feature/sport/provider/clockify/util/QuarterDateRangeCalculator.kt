package band.effective.office.backend.feature.sport.provider.clockify.util

import band.effective.office.backend.feature.sport.provider.clockify.constants.ClockifyConstants
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields

/**
 * Utility class for calculating quarter date ranges.
 * Provides methods to get current quarter start and end dates.
 */
object QuarterDateRangeCalculator {
    
    private val ISO_FORMATTER = DateTimeFormatter.ofPattern(ClockifyConstants.ISO_DATE_FORMAT)

    /**
     * Gets the current quarter date range in ISO format with time.
     * 
     * @return Pair of (startDate, endDate) in ISO format
     */
    fun getCurrentQuarterDateRange(): Pair<String, String> {
        val now = LocalDate.now()
        val quarterDates = getQuarterDates(now.year, now.monthValue)
        
        val startDateTime = ZonedDateTime.of(quarterDates.first.atStartOfDay(), ZoneOffset.UTC)
        val endDateTime = ZonedDateTime.of(quarterDates.second.atTime(23, 59, 59), ZoneOffset.UTC)
        
        return Pair(
            startDateTime.format(ISO_FORMATTER),
            endDateTime.format(ISO_FORMATTER)
        )
    }

    /**
     * Calculates the start and end dates of the quarter containing the given month.
     */
    private fun getQuarterDates(year: Int, month: Int): Pair<LocalDate, LocalDate> {
        val date = LocalDate.of(year, month, 1)
        val quarter = date.get(IsoFields.QUARTER_OF_YEAR)
        
        // Set to the first day of the quarter
        val startDate = date
            .with(IsoFields.QUARTER_OF_YEAR, quarter.toLong())
            .with(IsoFields.DAY_OF_QUARTER, 1)
        
        // Get the last day of the third month in the quarter
        val lastMonthOfQuarter = startDate.plusMonths(2)
        val endDate = lastMonthOfQuarter.withDayOfMonth(lastMonthOfQuarter.lengthOfMonth())
        
        return Pair(startDate, endDate)
    }
}