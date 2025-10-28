package band.effective.office.backend.feature.photos.provider.synology.constants

object SynologyApiConstants {
    // HTTP Headers
    const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    
    // Session Management
    const val SESSION_CACHE_KEY = "synology_session"
    
    // API Parameters
    const val AUTH_API = "SYNO.API.Auth"
    const val ALBUMS_API = "SYNO.Foto.Browse.Album"
    const val PHOTOS_API = "SYNO.Foto.Browse.Item"
    const val AUTH_SESSION = "FileStation"
    const val AUTH_FORMAT = "sid"
    const val AUTH_METHOD = "login"
    const val AUTH_INFO_METHOD = "info"
    const val LIST_METHOD = "list"
    
    // API Versions
    const val AUTH_VERSION = 3
    const val ALBUMS_VERSION = 2
    const val PHOTOS_VERSION = 1
    
    // Pagination
    const val DEFAULT_OFFSET = 0
    const val DEFAULT_ALBUMS_LIMIT = 100
    
    // Photo Types
    const val PHOTO_TYPE = "photo"
    
    // Additional Fields for Photo Requests
    const val PHOTO_ADDITIONAL_FIELDS = "[\"thumbnail\",\"resolution\",\"orientation\",\"video_convert\",\"video_meta\",\"provider_user_id\"]"
}