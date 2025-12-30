package band.effective.office.tablet.core.ui.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun getCurrentLanguageCode(): String = NSLocale.currentLocale.languageCode