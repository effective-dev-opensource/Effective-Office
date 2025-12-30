package band.effective.office.backend.feature.photo.saver.storage.synology.api

import band.effective.office.backend.feature.photo.saver.storage.synology.dto.SynologyAlbumsResponseDTO
import band.effective.office.backend.feature.photo.saver.storage.synology.dto.SynologyAuthResponseDTO
import band.effective.office.backend.feature.photo.saver.storage.synology.dto.SynologyCreateAlbumResponseDTO
import band.effective.office.backend.feature.photo.saver.storage.synology.dto.SynologyUploadPhotoResponseDTO
import band.effective.office.backend.feature.photo.saver.storage.synology.dto.SynologyAddPhotoResponseDTO
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.core.io.Resource

/**
 * Synology API interface using Spring Web Service.
 */
@HttpExchange
interface SynologyApi {

    @GetExchange("/webapi/auth.cgi")
    fun auth(
        @RequestParam("api") api: String,
        @RequestParam("version") version: Int,
        @RequestParam("method") method: String,
        @RequestParam("account") account: String,
        @RequestParam("passwd") passwd: String,
        @RequestParam("session") session: String,
        @RequestParam("format") format: String
    ): ResponseEntity<SynologyAuthResponseDTO>

    @GetExchange("/webapi/auth.cgi")
    fun checkSession(
        @RequestParam("api") api: String,
        @RequestParam("version") version: Int,
        @RequestParam("method") method: String,
        @RequestParam("session") session: String,
        @RequestParam("_sid") sid: String
    ): SynologyAuthResponseDTO

    @GetExchange("/webapi/entry.cgi")
    fun getAlbums(
        @RequestHeader("Cookie") cookie: String,
        @RequestParam("api") api: String,
        @RequestParam("version") version: Int,
        @RequestParam("method") method: String,
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int
    ): SynologyAlbumsResponseDTO

    @PostExchange("/webapi/entry.cgi/SYNO.Foto.Browse.NormalAlbum", contentType = "application/x-www-form-urlencoded")
    fun createAlbum(
        @RequestHeader("Cookie") cookie: String,
        @RequestHeader("X-Requested-With") xRequestedWith: String = "XMLHttpRequest",
        @RequestBody body: String
    ): SynologyCreateAlbumResponseDTO

    @PostExchange("/webapi/entry.cgi?api=SYNO.Foto.Upload.Item&method=upload&version=1", contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    fun uploadPhoto(
        @RequestHeader("Cookie") cookie: String,
        @RequestHeader("X-Requested-With") xRequestedWith: String = "XMLHttpRequest",
        @RequestPart("file") file: Resource,
        @RequestPart("name") name: String,
        @RequestPart("duplicate") duplicate: String
    ): SynologyUploadPhotoResponseDTO
    
    @PostExchange("/webapi/entry.cgi/SYNO.Foto.Browse.NormalAlbum", contentType = "application/x-www-form-urlencoded")
    fun addPhotoToAlbum(
        @RequestHeader("Cookie") cookie: String,
        @RequestHeader("X-Requested-With") xRequestedWith: String = "XMLHttpRequest",
        @RequestBody body: String
    ): SynologyAddPhotoResponseDTO
}
