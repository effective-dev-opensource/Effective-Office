package band.effective.office.tv.feature.selfUpdate.presentation

sealed interface UpdateIntent {
    data object CheckUpdate: UpdateIntent
    data object InstallUpdate: UpdateIntent
}