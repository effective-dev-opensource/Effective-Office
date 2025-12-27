package band.effective.office.tv.feature.stories.presentation.rating.common

/**
 * Returns the first name from a full name string or the provided fallback if fullName is null/blank.
 */
fun firstNameOf(fullName: String?, fallback: String? = ""): String {
    val name = fullName?.trim()
    if (name.isNullOrEmpty()) return fallback ?: ""
    return name.split(Regex("\\s+"))[0]
}
