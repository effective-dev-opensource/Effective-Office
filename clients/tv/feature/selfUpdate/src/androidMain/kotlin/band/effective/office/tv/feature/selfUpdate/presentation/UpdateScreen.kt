package band.effective.office.tv.feature.selfUpdate.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.components.TextButton
import band.effective.office.tv.core.ui.theme.AppTheme
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.update_download_button
import band.effective.office.tv.core.ui.update_download_title
import band.effective.office.tv.core.ui.update_search_button
import band.effective.office.tv.core.ui.update_search_title
import band.effective.office.tv.core.ui.update_version
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateScreen(updateComponent: UpdateComponent) {
    val state by updateComponent.state.collectAsState()
    UpdateScreen(
        versionNumber = state.updateInfo?.versionCode,
        loading = state.searching,
        downloading = state.downloading,
        sendIntent = updateComponent::sendIntent
    )
}

@Composable
private fun UpdateScreen(
    versionNumber: Int?,
    loading: Boolean,
    downloading: Boolean,
    sendIntent: (UpdateIntent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            loading -> Loader(title = stringResource(Res.string.update_search_title))
            downloading -> Loader(title = stringResource(Res.string.update_download_title))
            versionNumber != null -> UpdateInfo(versionNumber) { sendIntent(UpdateIntent.InstallUpdate) }
            else -> EmptyUpdateInfo { sendIntent(UpdateIntent.CheckUpdate) }
        }
    }
}

@Composable
private fun ColumnScope.Loader(title: String) {
    Text(title)
    Spacer(modifier = Modifier.height(LocalTvSizes.current.gapMedium))
    CircularProgressIndicator()
}

@Composable
private fun ColumnScope.UpdateInfo(
    versionNumber: Int,
    onRequestUpdate: () -> Unit
) {
    Text(stringResource(Res.string.update_version, versionNumber))
    Spacer(modifier = Modifier.height(LocalTvSizes.current.gapMedium))
    TextButton(
        text = stringResource(Res.string.update_download_button),
        onClick = onRequestUpdate,
    )
}

@Composable
private fun ColumnScope.EmptyUpdateInfo(
    onRequestInfo: () -> Unit
) {
    TextButton(
        text = stringResource(Res.string.update_search_button),
        onClick = onRequestInfo,
    )
}

@Preview(device = Devices.TV_720p, name = "Initial")
@Composable
private fun UpdateScreenInitialPreview() {
    AppTheme {
        Surface {
            UpdateScreen(
                versionNumber = 42,
                loading = false,
                downloading = false,
                sendIntent = {}
            )
        }
    }
}

@Preview(device = Devices.TV_720p, name = "Loading")
@Composable
private fun UpdateScreenLoadingPreview() {
    AppTheme {
        Surface {
            UpdateScreen(
                versionNumber = 42,
                loading = true,
                downloading = false,
                sendIntent = {}
            )
        }
    }
}

@Preview(device = Devices.TV_720p, name = "Update Found")
@Composable
private fun UpdateScreenUpdateFoundPreview() {
    AppTheme {
        Surface {
            UpdateScreen(
                versionNumber = 42,
                loading = false,
                downloading = false,
                sendIntent = {}
            )
        }
    }
}

@Preview(device = Devices.TV_720p, name = "Downloading")
@Composable
private fun UpdateScreenDownloadingPreview() {
    AppTheme {
        Surface {
            UpdateScreen(
                versionNumber = 42,
                loading = false,
                downloading = true,
                sendIntent = {}
            )
        }
    }
}