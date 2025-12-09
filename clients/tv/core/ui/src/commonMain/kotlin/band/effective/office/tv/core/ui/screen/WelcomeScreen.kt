package band.effective.office.tv.core.ui.screen

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
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
import band.effective.office.tv.core.ui.autoplay_menu
import band.effective.office.tv.core.ui.components.PlayButton
import band.effective.office.tv.core.ui.components.TextButton
import band.effective.office.tv.core.ui.components.CircleWithBlur
import band.effective.office.tv.core.ui.main_screen_description
import band.effective.office.tv.core.ui.start_autoplay
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.welcome_title
import org.jetbrains.compose.resources.stringResource

/**
 * Welcome screen - entry point of the TV application.
 */
@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onStartAutoplay: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
) {

    val focusRequester = remember { FocusRequester() }
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current
    val sizes = LocalTvSizes.current
    
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight

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
            Text(
                modifier = Modifier.width(sizes.menuTitleWidth),
                text = stringResource(Res.string.welcome_title),
                style = typography.displaySmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(sizes.gapMedium))
            
            // Description
            Text(
                modifier = Modifier.width(sizes.menuDescriptionWidth),
                text = stringResource(Res.string.main_screen_description),
                color = colors.textPrimary,
                style = typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(sizes.gapLarge))
            
            // Play button
            PlayButton(
                text = stringResource(Res.string.start_autoplay),
                onClick = onStartAutoplay,
                focusRequester = focusRequester,
            )
            
            Spacer(Modifier.height(sizes.gapSmall))
            
            // Settings button
            TextButton(
                text = stringResource(Res.string.autoplay_menu),
                onClick = onOpenMenu,
            )
        }
        
        LaunchedEffect(Unit) { 
            focusRequester.requestFocus() 
        }
    }
}



