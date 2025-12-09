package band.effective.office.shared.core.utils

/**
 * Returns the correct Russian declension for a number
 * 
 * Handles Russian number declension rules:
 * - Numbers ending in 1 (except 11): nominative case (1 час, 21 минута)
 * - Numbers ending in 2-4 (except 12-14): genitive case (2 часа, 23 минуты)
 * - Numbers ending in 0, 5-9, and 11-14: genitive plural (5 часов, 11 минут)
 */
fun getCorrectDeclension(
    number: Int,
    nominativeCase: String,
    genitive: String,
    genitivePlural: String
): String = if (number in 10..20) {
    genitivePlural
} else {
    when (number % 10) {
        0 -> genitivePlural
        1 -> nominativeCase
        2, 3, 4 -> genitive
        else -> genitivePlural
    }
}
