package band.effective.office.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvShapes
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.play_icon
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import org.jetbrains.compose.resources.painterResource

/**
 * Play button with icon.
 */
@Composable
fun PlayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    iconContentDescription: String? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current
    val sizes = LocalTvSizes.current
    val shapes = LocalTvShapes.current

        val animatedBackgroundColor by animateColorAsState(
            targetValue = if (isFocused) colors.primary else colors.secondary,
            label = "play_button_background"
        )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedBackgroundColor,
            contentColor = colors.onPrimary
        ),
        modifier = modifier
            .height(sizes.buttonHeightSmall)
            .width(sizes.startButtonWidth)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { isFocused = it.isFocused },
        shape = shapes.large,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(sizes.playButtonIconSize),
                painter = painterResource(Res.drawable.play_icon),
                contentDescription = iconContentDescription,
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(sizes.gapTiny))
            val textAlpha = if (isFocused) 1f else 0.5f
            Text(
                text = text,
                style = typography.labelMedium,
                color = colors.onPrimary.copy(alpha = textAlpha)
            )
        }
    }
}
