package band.effective.office.elevator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.office.elevator.ExtendedThemeColors
import band.effective.office.elevator.MainRes
import band.effective.office.elevator.ui.models.UserDataTextFieldType
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun UserInfoTextField(
    modifier: Modifier = Modifier,
    item: UserDataTextFieldType,
    error: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    text: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    filterInput: (String) -> String = { it }
) {
    var textValue by remember { mutableStateOf(text) }

    OutlinedTextField(
        value = textValue,
        modifier = modifier.fillMaxWidth(),
        onValueChange = { input ->
            val filtered = filterInput(input)
            if (filtered != textValue) {
                textValue = filtered
                onValueChange(filtered)
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp),
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Divider(
                    modifier = Modifier.height(28.dp).width(2.dp).clip(RoundedCornerShape(4.dp))
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    textValue = ""
                    onValueChange("")
                }
            ) {
                Icon(
                    painter = painterResource(MainRes.images.clear_icon),
                    contentDescription = null,
                )
            }
        },
        colors = OutlinedTextColorsSetup(),
        isError = error,
        visualTransformation = visualTransformation,
        placeholder = {
              Text(
                  text = stringResource(item.placeholder),
                  style = MaterialTheme.typography.button
              )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done)
    )
}
