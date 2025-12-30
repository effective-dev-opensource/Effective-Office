package band.effective.office.backend.feature.photo.saver.storage.synology.util

import band.effective.office.backend.feature.photo.saver.core.util.UrlUtils
import band.effective.office.backend.feature.photo.saver.storage.synology.constants.SynologyConstants

/**
 * Builder for Synology API request bodies.
 * Provides clean construction of URL-encoded request strings.
 */
object SynologyRequestBuilder {
    
    /**
     * Builds request body for creating a new album.
     */
    fun buildCreateAlbumBody(albumName: String): String =
        "api=${SynologyConstants.ALBUM_CREATE_API}" +
        "&method=${SynologyConstants.METHOD_CREATE}" +
        "&version=${SynologyConstants.ALBUM_CREATE_VERSION}" +
        "&name=${UrlUtils.urlEncode("\"$albumName\"")}" +
        "&item=${SynologyConstants.EMPTY_ITEM_LIST}"
    
    /**
     * Builds request body for adding photo to album.
     */
    fun buildAddToAlbumBody(itemId: Int, albumId: Int): String =
        "api=${SynologyConstants.ALBUM_CREATE_API}" +
        "&method=${SynologyConstants.METHOD_ADD_ITEM}" +
        "&version=${SynologyConstants.ADD_ITEM_VERSION}" +
        "&item=${UrlUtils.urlEncode("[$itemId]")}" +
        "&id=$albumId"
}
