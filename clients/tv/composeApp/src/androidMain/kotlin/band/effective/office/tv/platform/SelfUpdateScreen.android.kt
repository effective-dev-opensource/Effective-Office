package band.effective.office.tv.platform

import androidx.compose.runtime.Composable
import band.effective.office.tv.feature.selfUpdate.presentation.UpdateComponent
import band.effective.office.tv.feature.selfUpdate.presentation.UpdateScreen
import com.arkivanov.decompose.ComponentContext

@Composable
actual fun SelfUpdateScreen(componentContext: ComponentContext) {
    if (componentContext !is UpdateComponent) return
    UpdateScreen(componentContext)
}