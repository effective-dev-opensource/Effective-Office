package band.effective.office.tablet.core.ui.platform

import java.util.Locale

actual fun getCurrentLanguageCode(): String = Locale.getDefault().language
