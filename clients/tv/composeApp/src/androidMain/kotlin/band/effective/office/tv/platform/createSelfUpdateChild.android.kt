package band.effective.office.tv.platform

import band.effective.office.tv.feature.selfUpdate.presentation.UpdateComponent
import com.arkivanov.decompose.ComponentContext

actual fun createSelfUpdateChild(componentContext: ComponentContext): ComponentContext = UpdateComponent(componentContext)