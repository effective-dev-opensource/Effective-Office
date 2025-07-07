package band.effective.office.tablet.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.common.Loader

@Composable
fun LoadMainScreen() {
    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Loader()
    }
}