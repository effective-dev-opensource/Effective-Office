package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import band.effective.office.tv.core.ui.theme.AppTheme
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvSizes
import band.effective.office.tv.core.ui.theme.LocalTvTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TextWithIcon(
    iconVector: ImageVector? = null,
    iconPainter: Painter? = null,
    text: String,
    modifier: Modifier = Modifier,
    iconTint: Color? = null,
    iconSize: Dp? = null,
    textStyle: TextStyle? = null,
) {
    val sizes = LocalTvSizes.current
    val colors = LocalTvColorsPalette.current
    val typography = LocalTvTypography.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        when {
            iconVector != null -> {
                Icon(
                    imageVector = iconVector,
                    contentDescription = text,
                    tint = iconTint ?: colors.textSecondary,
                    modifier = Modifier.size(iconSize ?: sizes.iconSmall)
                )
            }

            iconPainter != null -> {
                Image(
                    painter = iconPainter,
                    contentDescription = text,
                    modifier = Modifier.size(iconSize ?: sizes.iconSmall)
                )
            }
        }

        Spacer(modifier = Modifier.width(sizes.gapMedium))

        Text(
            text = text,
            style = textStyle ?: typography.bodyMedium,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun TextWithIconPreview() {
    AppTheme {
        TextWithIcon(
            iconVector = Icons.Default.Info,
            text = "Main Text Content"
        )
    }
}
