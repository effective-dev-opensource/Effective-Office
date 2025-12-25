package band.effective.office.backend.feature.photo.saver.core.controller

import band.effective.office.backend.feature.photo.saver.core.service.PhotoSyncService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Photo Saver operations.
 * Provides endpoints for photo synchronization and management.
 */
@RestController
@RequestMapping("/v1/photo-saver")
@Tag(name = "Photo Saver", description = "Photo synchronization operations using configured provider")
class PhotoSaverController(
    private val photoSyncService: PhotoSyncService
) {

    /**
     * Triggers manual photo synchronization using the configured provider.
     */
    @PostMapping("/sync-photos")
    @Operation(
        summary = "Trigger photo sync",
        description = "Manually triggers synchronization of photos using the configured provider"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "202", description = "Photo sync triggered successfully")
        ]
    )
    fun syncPhotos(): ResponseEntity<Unit> {
        photoSyncService.syncPhotos()
        return ResponseEntity.accepted().build()
    }
}
