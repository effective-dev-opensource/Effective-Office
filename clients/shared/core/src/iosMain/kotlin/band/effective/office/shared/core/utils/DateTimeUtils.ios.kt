package band.effective.office.shared.core.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateFormatter

actual fun LocalDateTime.toLocalisedString(pattern: String): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = pattern

    val calendar = NSCalendar.currentCalendar
    val dateComponents = toNSDateComponents()

    val date = calendar.dateFromComponents(dateComponents) ?: return ""
    return dateFormatter.stringFromDate(date)
}
