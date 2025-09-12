package band.effective.office.backend.feature.notifications.service

import band.effective.office.backend.feature.notifications.repository.DeviceRepository
import band.effective.office.backend.feature.notifications.repository.entity.DeviceEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for managing devices
 */
@Service
class DeviceService(private val deviceRepository: DeviceRepository) {

    /**
     * Retrieves all registered devices
     */
    @Transactional(readOnly = true)
    fun getAllDevices(): List<DeviceEntity> {
        return deviceRepository.findAll()
    }

    /**
     * Checks if device exists by device ID
     *
     * @return true if device exists, false otherwise
     */
    @Transactional(readOnly = true)
    fun deviceExists(deviceId: String): Boolean {
        return deviceRepository.existsByDeviceId(deviceId)
    }
}