package band.effective.office.tv.core.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.components.CircleWithBlur
import band.effective.office.tv.core.ui.components.TextButton
import band.effective.office.tv.core.ui.error_description
import band.effective.office.tv.core.ui.error_title
import band.effective.office.tv.core.ui.retry_button
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import org.jetbrains.compose.resources.stringResource

/**
 * Error screen for TV application.
 * Displays error message with retry button.
 *
 * @param modifier Modifier for the screen
 * @param title Error title text (optional, uses default string resource if null)
 * @param description Error description text (optional, uses default string resource if null)
 * @param onRetry Callback when retry button is clicked
 */
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    onRetry: () -> Unit = {},
) {
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current
    val sizes = LocalTvSizes.current
    val focusRequester = remember { FocusRequester() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        val screenHeight = maxHeight

        // Decorative gradient circles
        CircleWithBlur(
            xOffset = sizes.blurCircleOffset,
            yOffset = -screenHeight / 2 - sizes.blurCircleOffset,
            color = colors.gradientPurple
        )
        CircleWithBlur(
            modifier = Modifier.alpha(0.8f),
            xOffset = (-sizes.blurCircleOffset),
            yOffset = -screenHeight / 2 - sizes.blurCircleOffset,
            color = colors.gradientOrange
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error title
            Text(
                modifier = Modifier.width(sizes.menuTitleWidth),
                text = title ?: stringResource(Res.string.error_title),
                style = typography.displaySmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(sizes.gapMedium))

            // Error description
            Text(
                modifier = Modifier.width(sizes.menuDescriptionWidth),
                text = description ?: stringResource(Res.string.error_description),
                color = colors.textSecondary,
                style = typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(sizes.gapLarge))

            // Retry button
            TextButton(
                text = stringResource(Res.string.retry_button),
                onClick = onRetry,
                focusRequester = focusRequester,
            )
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
}
