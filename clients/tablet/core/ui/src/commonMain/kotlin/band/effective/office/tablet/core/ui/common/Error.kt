package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.button.SuccessButton
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette

/**
 * Error screen to display when there's a connection issue
 *
 * @param resetRequest Callback to be invoked when the reset button is clicked
 * @param disconnectText Text to display for the disconnect message
 * @param resetText Text to display for the reset instructions
 * @param resetButtonText Text to display on the reset button
 */
@Composable
fun ErrorMainScreen(
    resetRequest: () -> Unit,
    disconnectText: String = "Connection lost",
    resetText: String = "Please try to reset the connection",
    resetButtonText: String = "Reset"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Note: Replace with actual image resource when available
        // Image(painter = painterResource(Res.image.disconnect), contentDescription = null)
        
        Spacer(modifier = Modifier.height(30.dp))
        
        Text(
            text = disconnectText,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(30.dp))
        
        Text(
            text = resetText,
            style = MaterialTheme.typography.headlineSmall,
            color = LocalCustomColorsPalette.current.secondaryTextAndIcon
        )
        
        Spacer(modifier = Modifier.height(60.dp))
        
        SuccessButton(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(0.3f),
            onClick = { resetRequest() }
        ) {
            Text(
                text = resetButtonText,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}