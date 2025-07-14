package band.effective.office.backend.feature.authorization.apikey.repository

import band.effective.office.backend.feature.authorization.apikey.entity.ApiKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for accessing API keys stored in the database.
 */
@Repository
interface ApiKeyRepository : JpaRepository<ApiKey, UUID> {
    
    /**
     * Finds an API key by its hashed value.
     *
     * @param keyValue The hashed API key value
     * @return The API key, or null if not found
     */
    fun findByKeyValue(keyValue: String): ApiKey?
}