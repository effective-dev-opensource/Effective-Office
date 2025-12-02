package band.effective.office.tv

import band.effective.office.tv.theme.AppTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * This is a temporary screen for testing the basic application structure.
 *
 * Design matches:
 * - Top gradients (purple and orange)
 * - Large text
 * - Description
 * - Two buttons
 */
@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onStartAutoplay: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.elevationBackground)
    ) {
        // Top gradients
            CircleWithBlur(xOffset = 150.dp, color = AppTheme.colors.purpleGradient) // purple
            CircleWithBlur(
                modifier = Modifier.alpha(0.8f),
                xOffset = (-150).dp,
                color = AppTheme.colors.orangeGradient // orange
            )
        
        // Content
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large title
            Text(
                modifier = Modifier.width(AppTheme.sizes.titleWidth),
                text = stringResource(Res.string.welcome_title),
                style = AppTheme.typography.headlineLarge,
                color = AppTheme.colors.primaryTextAndIcon,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppTheme.sizes.gapMedium))
            
            // Smaller description
            Text(
                modifier = Modifier.width(AppTheme.sizes.descriptionWidth),
                text = stringResource(Res.string.welcome_description),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.primaryTextAndIcon,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppTheme.sizes.gapLarge))

            
            // Button 1 - Start autoplay (orange/purple)
            Button(
                onClick = onStartAutoplay,
                modifier = Modifier
                    .height(AppTheme.sizes.buttonHeight)
                    .width(AppTheme.sizes.startButtonWidth)
                    .clip(shape = AppTheme.shapes.medium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.buttonPrimary
                )
            ) {
                Text(
                    text = stringResource(Res.string.start_autoplay),
                    color = AppTheme.colors.primaryTextAndIcon,
                    style = AppTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
            
            Spacer(modifier = Modifier.height(AppTheme.sizes.gapSmall))
            
            // Button 2 - Settings (gray, rounded)
            Button(
                onClick = onOpenSettings,
                modifier = Modifier
                    .height(AppTheme.sizes.buttonHeight)
                    .width(AppTheme.sizes.settingsButtonWidth)
                    .clip(shape = AppTheme.shapes.large),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.buttonSecondary
                )
            ) {
                Text(
                    text = stringResource(Res.string.settings),
                    color = AppTheme.colors.primaryTextAndIcon.copy(alpha = 0.5f),
                        style = AppTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        }
    }
}


/**
 * Blurred gradient circle for background
 */
@Composable
private fun CircleWithBlur(modifier: Modifier = Modifier, xOffset: Dp, color: Color) {
        Canvas(
        modifier = modifier
            .offset(xOffset, (-AppTheme.sizes.blurYOffset))
            .fillMaxSize(),
        onDraw = {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }
    )
}

