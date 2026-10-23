package band.effective.office.tablet.core.domain.useCase

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

/**
 * The local write and the answer that reconciles it have to be one unit: a caller cancelled
 * between them leaves the buffer with an event stuck in isLoading, which the room card hides and
 * the room picker counts as busy.
 */
internal suspend fun <T> withNonCancellableContext(block: suspend () -> T): T =
    withContext(NonCancellable) { block() }
