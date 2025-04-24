package band.effective.office.workTogether

import java.time.LocalDate

data class Teammate(
    val id: String,
    val name: String,
    val positions: List<String>,
    val employment: String,
    val startDate: LocalDate,
    val nextBDay: LocalDate,
    val workEmail: String?,
    val personalEmail: String,
    val duolingo: String?,
    val photo: String,
    val status: String,
) {
    fun isActive() = (employment.contains("Band") == true
            || employment.contains("Intern") == true)
            && status == "Active"
}
