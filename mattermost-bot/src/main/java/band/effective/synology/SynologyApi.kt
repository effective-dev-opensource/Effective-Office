package band.effective.synology

import band.effective.core.Either
import band.effective.core.ErrorReason
import band.effective.synology.models.respone.*
import okhttp3.Cookie
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface SynologyApi {
    @GET("/webapi/auth.cgi?api=SYNO.API.Auth&session=FileStation&format=cookie")
    suspend fun auth(
        @Query("version") version: Int,
        @Query("method") method: String,
        @Query("account") login: String,
        @Query("passwd") password: String
    ): Response<SynologyAuthResponse>

    @GET("/webapi/entry.cgi/SYNO.Foto.Browse.Album?api=SYNO.Foto.Browse.Album")
    suspend fun getAlbums(
        @Header("Cookie") cookie: String,
        @Query("version") version: Int,
        @Query("method") method: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Either<ErrorReason, SynologyAlbumsResponse>

    @Multipart
    @POST("/webapi/entry.cgi?api=SYNO.Foto.Upload.Item&method=upload&version=1")
    @Headers("X-Requested-With: XMLHttpRequest")
    suspend fun uploadPhoto(
        @Header("Cookie") cookie: String,
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("duplicate") duplicate: RequestBody
    ): Response<ResponseBody>

    @POST("/webapi/entry.cgi")
    @Headers(
            "X-Requested-With: XMLHttpRequest",
            "Accept-Encoding: gzip, deflate, br"
    )
    suspend fun uploadPhotoEither(
            @Header("Cookie") cookie: String,
            @Body body: RequestBody
    ): Either<ErrorReason, UploadPhotoResponse>

    @POST("/webapi/entry.cgi/SYNO.Foto.Browse.NormalAlbum")
    suspend fun addPhotoToAlbum(
        @Header("Cookie") cookie: String,
        @Body request: RequestBody
    ): Either<ErrorReason, AddPhotoToAlbumResponse>

    @POST("/webapi/entry.cgi/SYNO.Foto.Browse.NormalAlbum")
    @Headers("X-Requested-With: XMLHttpRequest")
    suspend fun createAlbum(
            @Header("Cookie") cookie: String,
            @Body request: RequestBody
    ): Either<ErrorReason, SynologyCreateAlbumsResponse>
}