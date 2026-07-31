package band.effective.office.tablet.core.ui.utils

// TODO: read the system locale. A meeting-room tablet is always Russian in practice, and Aurora
// does expose the locale through Qt — this belongs together with localising the dates (see
// DateTimeUtils.linux.kt).
actual fun getCurrentLanguageCode(): String = "ru"
