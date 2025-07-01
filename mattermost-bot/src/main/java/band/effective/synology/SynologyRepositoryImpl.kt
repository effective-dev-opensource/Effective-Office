package band.effective.synology

import band.effective.SynologySettings
import band.effective.core.Either
import band.effective.core.ErrorReason
import band.effective.core.moshi
import band.effective.core.synologyApi
import band.effective.synology.models.SynologyAlbumInfo
import band.effective.synology.models.respone.AddPhotoToAlbumResponse
import band.effective.synology.models.respone.SynologyAlbumsResponse
import band.effective.synology.models.respone.UploadPhotoResponse
import band.effective.utils.getEnv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import java.util.zip.GZIPInputStream

class SynologyRepositoryImpl : SynologyRepository {

    private var cookie: String? = null

    private var currentAlbumId: Int? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            login()
        }
    }

    private suspend fun login() {
        println("login in synology")
        val res = synologyApi.auth(
            version = 3,
            method = "login",
            login = getEnv(SynologySettings.synologyAccount),
            password = getEnv(SynologySettings.synologyPassword),
        )
        cookie = ""
        val headersCookie = res.headers().toMultimap()["Set-Cookie"]
        cookie = headersCookie?.reduce { first, second -> first + second }
    }

    private suspend fun getAlbums(): Either<ErrorReason, SynologyAlbumsResponse> =
        synologyApi.getAlbums(
            cookie = cookie.orEmpty(),
            version = 2,
            method = "list",
            offset = 0,
            limit = 100
        )

    private suspend fun uploadPhoto(
        filePart: MultipartBody.Part,
        namePart: RequestBody,
        dupPart: RequestBody
    ): Either<ErrorReason, UploadPhotoResponse> {
        val response = synologyApi.uploadPhoto(
            cookie = cookie.orEmpty(),
            file = filePart,
            name = namePart,
            duplicate = dupPart
        )
        val bodyString = response.body()?.string()

        if (!response.isSuccessful || bodyString == null) {
            return Either.Failure(ErrorReason.ServerError("HTTP ${response.code()}"))
        }

        val uploadResponse = try {
            moshi.adapter(UploadPhotoResponse::class.java).fromJson(bodyString)
                ?: return Either.Failure(ErrorReason.ServerError("Empty upload response"))
        } catch (e: Exception) {
            return Either.Failure(ErrorReason.ServerError("Parsing error: ${e.message}"))
        }

        return Either.Success(uploadResponse)
    }

    private suspend fun addPhotoToAlbums(
        albumId: Int,
        itemId: Int
    ): Either<ErrorReason, AddPhotoToAlbumResponse> {
        val requestBody = RequestBody.create(
            MediaType.parse("text/plane"),
            "api=SYNO.Foto.Browse.NormalAlbum&method=add_item&version=1&item=%5B$itemId%5D&id=$albumId"
        )
        return synologyApi.addPhotoToAlbum(request = requestBody, cookie = cookie.orEmpty())
    }

    override suspend fun uploadPhotoToAlbum(
        file: ByteArray,
        fileName: String,
        fileType: String
    ): Either<ErrorReason, UploadPhotoResponse> {
        if (cookie == null) login()

        if (currentAlbumId == null) {
            when (val albumsReq = getAlbums()) {
                is Either.Success -> {
                    println("get album success")
                    val albums = albumsReq.data.albumsData.albums
                    val currentAlbumName = currentAlbumName()
                    albums.find { album ->
                        album.name == currentAlbumName
                    }.let { album ->
                        currentAlbumId = if (album != null) album.id
                        else when (val createAlbum = createAlbum(currentAlbumName)) {
                            is Either.Success -> createAlbum.data.albumId
                            is Either.Failure -> throw Error("albums $currentAlbumName not found and cant be created ")
                        }
                    }
                }

                is Either.Failure -> {
                    return Either.Failure(albumsReq.error)
                }
            }
        }
        val (filePart, namePart, dupPart) = setRequestToUpload(file, fileName, fileType)
        val uploadResult = uploadPhoto(filePart, namePart, dupPart)

        if (uploadResult is Either.Failure) return uploadResult

        val itemId = (uploadResult as? Either.Success)?.data?.uploadedPhoto?.id
            ?: return Either.Failure(ErrorReason.ServerError("No item id in upload response"))

        val albumId = currentAlbumId ?: return Either.Failure(ErrorReason.ServerError("No album id"))

        val addResult = addPhotoToAlbums(albumId, itemId)
        if (addResult is Either.Failure) return addResult

        return uploadResult
    }

    private fun setRequestToUpload(file: ByteArray, fileName: String, fileType: String): Triple<MultipartBody.Part, RequestBody, RequestBody> {
        val filePart = MultipartBody.Part.createFormData(
            "file", fileName,
            RequestBody.create(MediaType.parse(fileType), file)
        )
        val namePart = RequestBody.create(MediaType.parse("text/plain"), "\"$fileName\"")
        val dupPart = RequestBody.create(MediaType.parse("text/plain"), "\"ignore\"")
        return Triple(filePart, namePart, dupPart)
    }


    private suspend fun createAlbum(albumName: String): Either<ErrorReason, SynologyAlbumInfo> {
        val requestBody = RequestBody.create(MediaType.get("text/plane"), "api=SYNO.Foto.Browse.NormalAlbum&method=create&version=1&name=%22$albumName%22&item=%5B%5D")
        return when (val album = synologyApi.createAlbum(cookie = cookie.orEmpty(), requestBody)) {
            is Either.Success -> {
                Either.Success(SynologyAlbumInfo(album.data.albumsData.album.id))
            }

            is Either.Failure -> {
                Either.Failure(album.error)
            }
        }
    }

    private fun currentAlbumName(): String {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return "${getEnv(SynologySettings.synologyAlbumTypeName)} $currentYear"
    }

    // This method remove headers from multipart, because synology photo don't work with these headers
    private fun removeHeaderFromRequestBody(delegate: RequestBody): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return null
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                delegate.writeTo(sink)
            }
        }
    }
}