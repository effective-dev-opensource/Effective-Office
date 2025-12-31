package band.effective.office.tv.feature.photos.data.mapper

import band.effective.office.tv.feature.photos.data.dto.PhotoDTO
import band.effective.office.tv.feature.photos.domain.model.Photo

object PhotoMapper {
    fun toDomain(dto: PhotoDTO): Photo? {
        val url = dto.thumbnailUrl ?: return null
        return Photo(url = url)
    }
}

