package band.effective.office.tv.feature.stories.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.theme.LocalTvShapes
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay

@Composable
fun WarningBanner(
    warnings: List<String>,
    modifier: Modifier = Modifier,
    autoDismissMillis: Long = 15_000 // 15 sec
) {
    var visible by remember(warnings) { mutableStateOf(warnings.isNotEmpty()) }
    val title = stringResource(Res.string.stories_warning_partial)

    LaunchedEffect(warnings) {
        if (warnings.isNotEmpty()) {
            visible = true
            delay(autoDismissMillis)
            visible = false
        } else {
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible && warnings.isNotEmpty(),
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 }
    ) {
        Surface(
            modifier = modifier.clip(LocalTvShapes.current.large),
            color = Color.Black.copy(alpha = 0.55f),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = LocalTvTypography.current.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
                warnings.forEach {
                    Text(
                        text = "• $it",
                        style = LocalTvTypography.current.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}


