package band.effective.office.tablet.core.ui.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * A button with a rounded corner shape for success actions
 *
 * @param modifier Modifier to be applied to the button
 * @param onClick Callback to be invoked when the button is clicked
 * @param enable Whether the button is enabled
 * @param content Content to be displayed inside the button
 */
@Composable
fun SuccessButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enable: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier.clip(RoundedCornerShape(100.dp)),
        enabled = enable,
        onClick = { onClick() }
    ) {
        content()
    }
}