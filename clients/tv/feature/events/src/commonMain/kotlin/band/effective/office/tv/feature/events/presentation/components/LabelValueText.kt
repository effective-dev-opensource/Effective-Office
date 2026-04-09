package band.effective.office.tv.feature.events.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.core.ui.theme.LocalTvTypography

@Composable
fun LabelValueText(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelStyle: TextStyle = LocalTvTypography.current.bodyMedium,
    valueStyle: TextStyle = LocalTvTypography.current.bodyMedium,
) {
    val colors = LocalTvColorsPalette.current

    val annotatedString =
        remember(label, value, labelStyle, valueStyle, colors) {
        buildAnnotatedString {
            withStyle(labelStyle.toSpanStyle().copy(color = colors.textSecondary)) {
                append("$label: ")
            }

            withStyle(valueStyle.toSpanStyle().copy(color = colors.textPrimary)) {
                append(value)
            }
        }
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        style = valueStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
