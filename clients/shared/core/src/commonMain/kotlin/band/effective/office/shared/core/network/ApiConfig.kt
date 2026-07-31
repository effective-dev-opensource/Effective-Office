package band.effective.office.shared.core.network

/**
 * The API url and key as a single type.
 *
 * They cannot be kept as two `String`s under `named("ApiUrl")` / `named("ApiKey")` qualifiers:
 * koin keeps the parameters of the graph currently being created on a stack and matches them
 * **by type**, ignoring qualifiers. The api clients build their client lazily, in the
 * constructor (`HttpClientProvider.create()`), so it matters which graph reaches them first.
 * When that is a graph carrying a String parameter (`parametersOf(event, roomName)` in the
 * booking editor), the room name arrives instead of the url and the key: the request goes to
 * `http://localhost/` with `Authorization: Bearer Sync` and the organizer list comes back
 * empty. It is a lottery of initialization order, too — whichever graph gets there first has
 * its config cached in the singleton for the rest of the process.
 *
 * A dedicated type cannot collide with the parameters of any graph.
 */
data class ApiConfig(
    val url: String,
    val key: String,
)
