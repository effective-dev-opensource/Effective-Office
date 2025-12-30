package band.effective.office.tv.feature.photos.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import band.effective.office.tv.feature.photos.Res
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.feature.photos.photos_empty_title

@Composable
fun EmptyPhotosScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.photos_empty_title),
            style = LocalTvTypography.current.displaySmall,
            color = LocalTvColorsPalette.current.textPrimary
        )
    }
}
