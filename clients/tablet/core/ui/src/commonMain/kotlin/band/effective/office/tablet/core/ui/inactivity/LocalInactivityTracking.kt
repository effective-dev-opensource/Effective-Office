package band.effective.office.tablet.core.ui.inactivity

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalInactivityTracking: ProvidableCompositionLocal<InactivityTracking> =
    staticCompositionLocalOf { error("No InactivityTracking provided") }
