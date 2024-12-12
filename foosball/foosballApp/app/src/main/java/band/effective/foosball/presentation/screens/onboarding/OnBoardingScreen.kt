package band.effective.foosball.presentation.screens.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import band.effective.foosball.ui.theme.OrangeCircle
import band.effective.foosball.ui.theme.PurpleCircle
import com.example.effectivefoosball.R

@Composable
fun OnBoardingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "CircleAnim")

    val ball1X by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball1x"
    )

    val ball1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball1y"
    )

    val ball2X by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball2x"
    )

    val ball2Y by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball2y"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.maxDimension
            val ball1Offset = Offset(ball1X * size.width, ball1Y * size.height)
            val ball2Offset = Offset(ball2X * size.width, ball2Y * size.height)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(OrangeCircle, Color.Transparent),
                    center = ball1Offset,
                    radius = radius * 0.5f
                ),
                radius = radius,
                center = ball1Offset
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PurpleCircle, Color.Transparent),
                    center = ball2Offset,
                    radius = radius * 0.5f
                ),
                radius = radius,
                center = ball2Offset
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .width(500.dp)
                    .height(500.dp),
                painter = painterResource(id = R.drawable.effective_logo),
                contentDescription = null
            )
        }
    }
}
