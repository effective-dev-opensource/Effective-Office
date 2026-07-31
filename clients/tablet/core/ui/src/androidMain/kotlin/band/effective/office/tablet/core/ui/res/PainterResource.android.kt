package band.effective.office.tablet.core.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource as composePainterResource
import org.jetbrains.compose.resources.vectorResource as composeVectorResource

@Composable
actual fun painterResource(resource: DrawableResource): Painter = composePainterResource(resource)

@Composable
actual fun vectorResource(resource: DrawableResource): ImageVector = composeVectorResource(resource)
