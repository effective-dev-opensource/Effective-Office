package band.effective.office.tv.utils

import band.effective.office.tv.BuildKonfig

/**
 * Common implementation for debug flag backed by BuildKonfig.
 */
val isDebug: Boolean = BuildKonfig.IS_DEBUG
