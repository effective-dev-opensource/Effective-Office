package band.effective.office.backend.feature.photo.saver.storage.synology.constants

/**
 * Constants for Synology API integration.
 */
object SynologyConstants {
    // API Parameters
    const val AUTH_API = "SYNO.API.Auth"
    const val ALBUMS_API = "SYNO.Foto.Browse.Album"
    const val ALBUM_CREATE_API = "SYNO.Foto.Browse.NormalAlbum"
    const val UPLOAD_API = "SYNO.Foto.Upload.Item"
    
    const val AUTH_SESSION = "FileStation"
    const val AUTH_FORMAT = "cookie"
    
    // API Methods
    const val METHOD_LOGIN = "login"
    const val METHOD_INFO = "info"
    const val METHOD_LIST = "list"
    const val METHOD_CREATE = "create"
    const val METHOD_UPLOAD = "upload"
    const val METHOD_ADD_ITEM = "add_item"
    
    // API Versions
    const val AUTH_VERSION = 3
    const val ALBUMS_VERSION = 2
    const val ALBUM_CREATE_VERSION = 1
    const val UPLOAD_VERSION = 1
    const val ADD_ITEM_VERSION = 1
    
    // Pagination
    const val DEFAULT_OFFSET = 0
    const val DEFAULT_ALBUMS_LIMIT = 100
    
    // Upload Parameters
    const val DUPLICATE_POLICY = "\"ignore\""
    
    /**
     * Empty array in URL-encoded form: [] → %5B%5D
     * Used when creating a new album with no initial items.
     */
    const val EMPTY_ITEM_LIST = "%5B%5D"
    
    // HTTP Headers
    const val HEADER_COOKIE = "Cookie"
    const val HEADER_REQUESTED_WITH = "X-Requested-With"
    const val HEADER_REQUESTED_WITH_VALUE = "XMLHttpRequest"
    const val HEADER_ACCEPT_ENCODING = "Accept-Encoding"
    const val HEADER_ACCEPT_ENCODING_VALUE = "gzip, deflate, br"
    
    // Session cache
    const val SESSION_CACHE_KEY = "synology_session"
    
    // Error codes for session-related issues
    const val ERROR_SESSION_EXPIRED = 105        // Session does not exist
    const val ERROR_SESSION_INTERRUPTED = 106    // Session interrupted
    
    // Retry settings
    const val MAX_RETRY_ATTEMPTS = 1
    
    /**
     * Checks if error code indicates a session-related problem.
     * Session errors: 105 (session does not exist), 106 (session interrupted)
     */
    fun isSessionError(errorCode: Int?): Boolean {
        return errorCode in listOf(ERROR_SESSION_EXPIRED, ERROR_SESSION_INTERRUPTED)
    }
}
