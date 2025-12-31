package band.effective.office.backend.feature.photos.core.controller

import band.effective.office.backend.feature.photos.core.dto.PhotosDataDTO
import band.effective.office.backend.feature.photos.core.dto.PhotosResponseDTO
import band.effective.office.backend.feature.photos.core.dto.toDTO
import band.effective.office.backend.feature.photos.core.service.PhotoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
 

/**
 * REST controller for managing photos.
 */
@RestController
@RequestMapping("/v1/photos")
@Tag(
    name = "Photos", 
    description = "API for retrieving photos from various providers"
)
class PhotoController(
    private val photoService: PhotoService
) {

    /**
     * Get photos from the configured provider.
     *
     * @param limit Maximum number of photos to retrieve
     * @return Response containing photos and metadata
     */
    @GetMapping
    @Operation(
        summary = "Get photos",
        description = "Retrieves photos from the configured photo provider"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Photos retrieved successfully",
                content = [Content(schema = Schema(implementation = PhotosResponseDTO::class))]
            )
        ]
    )
    fun getPhotos(
        @Parameter(description = "Maximum number of photos to retrieve")
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<PhotosResponseDTO> {
        val photos = photoService.getPhotos(limit)
        val totalCount = photoService.getPhotosCount()

        val response = PhotosResponseDTO(
            success = true,
            message = "Photos retrieved successfully",
            data = PhotosDataDTO(
                photos = photos.map { it.toDTO() },
                totalCount = totalCount,
                limit = limit
            )
        )
        return ResponseEntity.ok(response)
    }

}