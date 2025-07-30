package band.effective.office.tablet.core.ui.utils

import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale


import kotlinx.datetime.toNSDateComponents
import platform.Foundation.*

actual fun LocalDateTime.toLocalisedString(pattern: String): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = pattern
    dateFormatter.locale = NSLocale.currentLocale

    val calendar = NSCalendar.currentCalendar
    val dateComponents = toNSDateComponents()

    val date = calendar.dateFromComponents(dateComponents) ?: NSDate()
    return dateFormatter.stringFromDate(date)
}

actual fun getCurrentLanguageCode(): String = NSLocale.currentLocale.languageCode