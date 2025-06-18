package band.effective.office.backend.feature.booking.calendar.google

import band.effective.office.backend.feature.booking.core.domain.model.RecurrenceModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.TimeZone

/**
 * Utility class for converting between RecurrenceModel and Google Calendar recurrence rules.
 */
object RecurrenceRuleConverter {

    private const val UNTIL_FORMAT = "yyyyMMdd'T'HHmmss'Z'"

    /**
     * Converts a RecurrenceModel to a Google Calendar recurrence rule string.
     *
     * @param recurrence The RecurrenceModel to convert
     * @return A list containing the RRULE string for Google Calendar
     */
    fun toGoogleRecurrenceRule(recurrence: RecurrenceModel): List<String> {
        val rule = StringBuilder("RRULE:FREQ=${recurrence.freq}")
        
        recurrence.interval?.let { rule.append(";INTERVAL=$it") }
        recurrence.count?.let { rule.append(";COUNT=$it") }
        recurrence.until?.let { rule.append(";UNTIL=${formatUntil(it)}") }
        
        if (recurrence.byDay.isNotEmpty()) {
            rule.append(";BYDAY=")
            rule.append(recurrence.byDay.joinToString(",") { dayNumberToString(it) })
        }
        
        if (recurrence.byMonth.isNotEmpty()) {
            rule.append(";BYMONTH=")
            rule.append(recurrence.byMonth.joinToString(","))
        }
        
        if (recurrence.byYearDay.isNotEmpty()) {
            rule.append(";BYYEARDAY=")
            rule.append(recurrence.byYearDay.joinToString(","))
        }
        
        if (recurrence.byHour.isNotEmpty()) {
            rule.append(";BYHOUR=")
            rule.append(recurrence.byHour.joinToString(","))
        }
        
        return listOf(rule.toString())
    }

    /**
     * Converts a Google Calendar recurrence rule string to a RecurrenceModel.
     *
     * @param recurrenceRules The list of recurrence rules from Google Calendar
     * @return The converted RecurrenceModel, or null if the input is null or empty
     */
    fun fromGoogleRecurrenceRule(recurrenceRules: List<String>?): RecurrenceModel? {
        if (recurrenceRules.isNullOrEmpty()) {
            return null
        }
        
        // Parse the first rule (we only support one rule for now)
        val rule = recurrenceRules[0]
        
        // Extract the part after "RRULE:"
        val ruleContent = rule.substringAfter("RRULE:", "")
        if (ruleContent.isEmpty()) {
            return null
        }
        
        // Split by semicolon to get individual parameters
        val params = ruleContent.split(";")
        
        var freq = "DAILY" // Default
        var interval: Int? = null
        var count: Int? = null
        var until: Long? = null
        val byDay = mutableListOf<Int>()
        val byMonth = mutableListOf<Int>()
        val byYearDay = mutableListOf<Int>()
        val byHour = mutableListOf<Int>()
        
        // Parse each parameter
        for (param in params) {
            val parts = param.split("=")
            if (parts.size != 2) continue
            
            val key = parts[0]
            val value = parts[1]
            
            when (key) {
                "FREQ" -> freq = value
                "INTERVAL" -> interval = value.toIntOrNull()
                "COUNT" -> count = value.toIntOrNull()
                "UNTIL" -> until = parseUntil(value)
                "BYDAY" -> byDay.addAll(value.split(",").map { dayStringToNumber(it) })
                "BYMONTH" -> byMonth.addAll(value.split(",").mapNotNull { it.toIntOrNull() })
                "BYYEARDAY" -> byYearDay.addAll(value.split(",").mapNotNull { it.toIntOrNull() })
                "BYHOUR" -> byHour.addAll(value.split(",").mapNotNull { it.toIntOrNull() })
            }
        }
        
        return RecurrenceModel(
            interval = interval,
            freq = freq,
            count = count,
            until = until,
            byDay = byDay,
            byMonth = byMonth,
            byYearDay = byYearDay,
            byHour = byHour
        )
    }
    
    /**
     * Formats a timestamp as a UTC date string in the format required by Google Calendar.
     *
     * @param timestamp The timestamp to format
     * @return The formatted date string
     */
    private fun formatUntil(timestamp: Long): String {
        val sdf = SimpleDateFormat(UNTIL_FORMAT)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Parses a UTC date string in the format used by Google Calendar to a timestamp.
     *
     * @param dateStr The date string to parse
     * @return The parsed timestamp, or null if parsing fails
     */
    private fun parseUntil(dateStr: String): Long? {
        return try {
            val sdf = SimpleDateFormat(UNTIL_FORMAT)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(dateStr).time
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Converts a day number (1-7) to the corresponding day string used by Google Calendar.
     *
     * @param dayNumber The day number (1=Monday, 7=Sunday)
     * @return The day string (MO, TU, WE, TH, FR, SA, SU)
     */
    private fun dayNumberToString(dayNumber: Int): String {
        return when (dayNumber) {
            1 -> "MO"
            2 -> "TU"
            3 -> "WE"
            4 -> "TH"
            5 -> "FR"
            6 -> "SA"
            7 -> "SU"
            else -> "MO" // Default to Monday for invalid values
        }
    }
    
    /**
     * Converts a day string used by Google Calendar to the corresponding day number.
     *
     * @param dayString The day string (MO, TU, WE, TH, FR, SA, SU)
     * @return The day number (1=Monday, 7=Sunday)
     */
    private fun dayStringToNumber(dayString: String): Int {
        return when (dayString) {
            "MO" -> 1
            "TU" -> 2
            "WE" -> 3
            "TH" -> 4
            "FR" -> 5
            "SA" -> 6
            "SU" -> 7
            else -> 1 // Default to Monday for invalid values
        }
    }
}