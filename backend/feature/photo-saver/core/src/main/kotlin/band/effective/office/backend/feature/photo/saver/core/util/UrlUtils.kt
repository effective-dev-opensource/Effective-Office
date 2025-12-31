package band.effective.office.backend.feature.photo.saver.core.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Utility functions for URL encoding operations.
 */
object UrlUtils {
    
    /**
     * URL-encodes a string value using UTF-8 encoding.
     * 
     * @param value The string to encode
     * @return URL-encoded string
     */
    fun urlEncode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
}
