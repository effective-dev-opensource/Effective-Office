package band.effective.office.tablet.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * A generic [NavType] for any `@Serializable` type, used in the route `typeMap` so that
 * complex payloads (`EventInfo`, `RoomInfo`, `List<RoomInfo>`) can travel inside type-safe routes.
 *
 * The value is JSON-encoded then URL-safe base64-encoded, so it is safe to embed in a route string.
 */
@OptIn(ExperimentalEncodingApi::class)
inline fun <reified T> serializableNavType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
): NavType<T> = object : NavType<T>(isNullableAllowed) {

    override fun put(bundle: SavedState, key: String, value: T) {
        bundle.write { putString(key, serializeAsValue(value)) }
    }

    override fun get(bundle: SavedState, key: String): T =
        parseValue(bundle.read { getString(key) })

    override fun parseValue(value: String): T =
        json.decodeFromString(Base64.UrlSafe.decode(value).decodeToString())

    override fun serializeAsValue(value: T): String =
        Base64.UrlSafe.encode(json.encodeToString(value).encodeToByteArray())
}
