package band.effective.office.tablet.core.ui.utils

// TODO: читать локаль системы. Планшет в переговорке всегда русский, а Аврора локаль через Qt
// отдаёт — до этого руки дойдут вместе с локализацией дат (см. DateTimeUtils.linux.kt).
actual fun getCurrentLanguageCode(): String = "ru"
