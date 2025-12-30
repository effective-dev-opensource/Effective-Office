package band.effective.office.backend.feature.teammates.core.domain

import band.effective.office.backend.feature.teammates.core.domain.model.Teammate
import band.effective.office.backend.feature.teammates.core.domain.model.TeammateScore

/**
 * Interface for teammate providers.
 * This interface defines the operations that teammate providers must implement.
 */
interface TeammateProvider {
    /**
     * Retrieves teammates with optional filters.
     * @param active Filter only active teammates
     * @param employment Filter by employment types (null = no filter, list = OR logic)
     */
    fun getTeammates(active: Boolean, employment: List<String>? = null): List<Teammate>
    
    /**
     * Retrieves teammate scores from the same Notion database.
     */
    fun getTeammateScores(): List<TeammateScore>
}