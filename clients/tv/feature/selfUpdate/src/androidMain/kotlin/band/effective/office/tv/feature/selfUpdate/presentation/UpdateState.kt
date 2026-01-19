package band.effective.office.tv.feature.selfUpdate.presentation

import band.effective.office.shared.core.selfUpdate.domain.UpdateInfo

data class UpdateState(
    val updateInfo: UpdateInfo?,
    val downloading: Boolean,
    val searching: Boolean,
){
    companion object{
        val defaultState = UpdateState(updateInfo = null, downloading = false, searching = false)
    }
}
