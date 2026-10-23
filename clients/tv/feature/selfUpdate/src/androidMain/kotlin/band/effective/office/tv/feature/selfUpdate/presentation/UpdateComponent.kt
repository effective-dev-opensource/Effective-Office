package band.effective.office.tv.feature.selfUpdate.presentation

import band.effective.office.shared.core.selfUpdate.domain.SelfUpdateInteractor
import band.effective.office.tv.core.ui.utils.componentCoroutineScope
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UpdateComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent {
    private val scope = componentCoroutineScope()
    val updateInteractor: SelfUpdateInteractor by inject()

    private val mutableState = MutableStateFlow(UpdateState.defaultState)
    val state = mutableState.asStateFlow()

    fun sendIntent(intent: UpdateIntent) {
        when (intent) {
            UpdateIntent.CheckUpdate -> checkUpdate()
            UpdateIntent.InstallUpdate -> install()
        }
    }

    private fun checkUpdate() = scope.launch(Dispatchers.IO) {
        mutableState.update { it.copy(searching = true) }
        val info = updateInteractor.getUpdateInfo()
        mutableState.update {
            it.copy(
                updateInfo = info ?: it.updateInfo,
                searching = false
            )
        }
    }

    private fun install() = scope.launch {
        mutableState.update { it.copy(downloading = true) }
        updateInteractor.downloadAndInstallUpdate()
        mutableState.update { it.copy(downloading = false) }
    }

}