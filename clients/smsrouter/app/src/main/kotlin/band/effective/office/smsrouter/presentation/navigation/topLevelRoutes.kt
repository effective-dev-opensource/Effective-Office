package band.effective.office.smsrouter.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings

val topLevelRoutes = listOf(
    TopLevelRoute("Messages", Routes.Messages, Icons.Default.Menu),
    TopLevelRoute("Settings", Routes.Settings, Icons.Default.Settings)
)