package band.effective.office.shared.core.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

actual fun LocalDateTime.toLocalisedString(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return this.toJavaLocalDateTime().format(formatter)
}