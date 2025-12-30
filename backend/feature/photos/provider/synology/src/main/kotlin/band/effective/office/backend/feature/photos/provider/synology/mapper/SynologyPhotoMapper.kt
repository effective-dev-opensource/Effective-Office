package band.effective.office.backend.feature.photos.provider.synology.mapper

import band.effective.office.backend.feature.photos.provider.synology.dto.PhotoInfoDTO
import band.effective.office.backend.feature.photos.provider.synology.dto.SynologyPhotoDTO

/**
 * Mapper for converting Synology photo data to DTOs.
 */
object SynologyPhotoMapper {
    
    fun toPhotoModel(photoInfo: PhotoInfoDTO, sid: String, baseUrl: String): SynologyPhotoDTO {
        val size = when {
            photoInfo.additional.thumbnail.sizeXl == "ready" -> "xl"
            photoInfo.additional.thumbnail.sizeM == "ready" -> "m"
            photoInfo.additional.thumbnail.sizeSm == "ready" -> "sm"
            else -> "sm"
        }
        
        val thumbnailUrl = "$baseUrl/webapi/entry.cgi/?cache_key=${photoInfo.additional.thumbnail.cacheKey}&id=${photoInfo.id}&api=SYNO.Foto.Thumbnail&method=get&version=1&type=unit&size=$size&_sid=$sid"
        
        return SynologyPhotoDTO(photoThumb = thumbnailUrl)
    }
}