package band.effective.office.tablet.core.domain.util

fun getCorrectDeclension(
    number: Int, nominativeCase: String, genitive: String, genitivePlural: String
): String = if (number in 10..20) genitivePlural
else when (number % 10) {
    0 -> genitivePlural
    1 -> nominativeCase
    2, 3, 4 -> genitive
    else -> genitivePlural
}