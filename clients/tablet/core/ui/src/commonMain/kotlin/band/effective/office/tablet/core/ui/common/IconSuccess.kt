package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.check
import band.effective.office.tablet.core.ui.res.vectorResource
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette

@Composable
fun IconSuccess() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.check),
            contentDescription = "Check",
            modifier = Modifier.size(60.dp),
            tint = LocalCustomColorsPalette.current.onSuccess
        )
    }
}