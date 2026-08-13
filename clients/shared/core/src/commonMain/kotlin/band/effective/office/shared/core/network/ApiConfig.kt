package band.effective.office.shared.core.network

/**
 * The api url and key as one type.
 *
 * Not two `String` singletons under qualifiers: koin matches the parameters of the graph being
 * created by type and ignores the qualifier, so a graph with a `String` parameter would hand its
 * own string to the lazily built api client, and the singleton would cache it for good.
 */
data class ApiConfig(
    val url: String,
    val key: String,
)
