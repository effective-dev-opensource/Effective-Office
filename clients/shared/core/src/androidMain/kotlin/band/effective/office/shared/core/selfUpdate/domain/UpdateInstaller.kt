package band.effective.office.shared.core.selfUpdate.domain

interface UpdateInstaller {
    fun install(path: String): Result<Unit>
}