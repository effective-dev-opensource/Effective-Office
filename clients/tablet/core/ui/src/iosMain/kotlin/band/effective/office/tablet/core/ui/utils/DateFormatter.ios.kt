package band.effective.office.tablet.core.ui.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getCurrentLanguageCode(): String = NSLocale.currentLocale.languageCode ?: "en"