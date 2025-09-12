package band.effective.office.tablet.utils

enum class MessageType(val value: String) {
    KIOSK_TOGGLE("KIOSK_TOGGLE"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromString(value: String?): MessageType {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}