package band.effective.office.tablet.feature.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import band.effective.office.settings.generated.resources.Res
import band.effective.office.settings.generated.resources.choose_room
import band.effective.office.tablet.core.ui.theme.CustomDarkColors
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h6_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChooseButtonView(
    modifier: Modifier,
    nameRoom: String,
    onSaveData: () -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }
    val colorButton = if (isPressed.value)
        LocalCustomColorsPalette.current.pressedPrimaryButton else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.4f),
            elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
            colors = ButtonDefaults.buttonColors(colorButton),
            shape = RoundedCornerShape(100),
            onClick = {
                isPressed.value = !isPressed.value
                onSaveData()
            }
        ) {
            Text(
                text = stringResource(Res.string.choose_room, nameRoom),
                style = MaterialTheme.typography.h6_button,
                color = when (LocalCustomColorsPalette.current) {
                    CustomDarkColors -> LocalCustomColorsPalette.current.primaryTextAndIcon
                    else -> MaterialTheme.colorScheme.background
                }
            )
        }
    }
}