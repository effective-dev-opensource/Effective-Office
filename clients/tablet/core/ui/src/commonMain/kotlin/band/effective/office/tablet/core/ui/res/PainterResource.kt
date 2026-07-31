package band.effective.office.tablet.core.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

/**
 * A facade over drawable resource loading.
 *
 * The signatures match `org.jetbrains.compose.resources.painterResource` / `vectorResource`
 * exactly, so switching a call site is a change of import and nothing else. On Android and iOS
 * the implementation simply delegates to compose-resources; the point of the facade is that a
 * platform whose resource loader does not handle everything can supply its own.
 */
@Composable
expect fun painterResource(resource: DrawableResource): Painter

/**
 * See [painterResource].
 */
@Composable
expect fun vectorResource(resource: DrawableResource): ImageVector
