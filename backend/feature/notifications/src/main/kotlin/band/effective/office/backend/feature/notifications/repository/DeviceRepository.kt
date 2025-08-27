package band.effective.office.backend.feature.notifications.repository

import band.effective.office.backend.feature.notifications.repository.entity.DeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for working with devices
 */
@Repository
interface DeviceRepository : JpaRepository<DeviceEntity, UUID> {

    /**
     * Check if device exists by device_id
     * @return true if device exists, false otherwise
     */
    fun existsByDeviceId(deviceId: String): Boolean
}