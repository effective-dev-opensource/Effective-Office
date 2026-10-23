package band.effective.office.tablet.core.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

/**
 * Signature-for-signature copy of `org.jetbrains.compose.resources.painterResource`, so a call site
 * changes its import and nothing else. The seam lets a platform whose resource loader does not
 * handle every format we ship supply its own decoder.
 */
@Composable
expect fun painterResource(resource: DrawableResource): Painter

/**
 * See [painterResource].
 */
@Composable
expect fun vectorResource(resource: DrawableResource): ImageVector
