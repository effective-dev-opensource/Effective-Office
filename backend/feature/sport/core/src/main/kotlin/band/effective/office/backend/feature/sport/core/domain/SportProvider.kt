package band.effective.office.backend.feature.sport.core.domain

import band.effective.office.backend.feature.sport.core.domain.model.SportUser

/**
 * Interface for sport providers.
 * This interface defines the operations that sport providers must implement.
 */
interface SportProvider {
    /**
     * Retrieves sport users with their time tracking data.
     */
    fun getSportUsers(): List<SportUser>
}
