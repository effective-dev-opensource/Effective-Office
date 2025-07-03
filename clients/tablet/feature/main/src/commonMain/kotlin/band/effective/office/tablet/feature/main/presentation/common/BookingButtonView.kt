package band.effective.office.tablet.feature.main.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.common.Loader
import band.effective.office.tablet.core.ui.theme.CustomDarkColors
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h6_button

@Composable
fun BookingButtonView(
    modifier: Modifier,
    shape: RoundedCornerShape,
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
) {
    val isPressed = remember { mutableStateOf(false) }
    val colorButton =
        if (isPressed.value) LocalCustomColorsPalette.current.pressedPrimaryButton else MaterialTheme.colorScheme.primary

    Button(
        modifier = modifier,
        elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
        colors = ButtonDefaults.buttonColors(colorButton),
        shape = shape,
        onClick = {
            isPressed.value = !isPressed.value
            onClick()
        }) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) Loader()
            else Text(
                text = text,
                style = MaterialTheme.typography.h6_button,
                color = when (LocalCustomColorsPalette.current) {
                    CustomDarkColors -> LocalCustomColorsPalette.current.primaryTextAndIcon
                    else -> MaterialTheme.colorScheme.background
                }
            )
        }
    }
}