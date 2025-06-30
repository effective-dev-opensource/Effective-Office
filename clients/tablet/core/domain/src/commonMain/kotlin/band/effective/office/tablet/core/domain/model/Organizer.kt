package band.effective.office.tablet.core.domain.model

data class Organizer(val fullName: String, val id: String, val email: String?) {
    companion object {
        val default = Organizer(
            fullName = "", id = "", email = null
        )
    }
}

