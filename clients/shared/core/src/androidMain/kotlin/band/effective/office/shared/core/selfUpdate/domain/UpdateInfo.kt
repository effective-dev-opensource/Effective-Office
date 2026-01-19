package band.effective.office.shared.core.selfUpdate.domain

import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfo(
    val versionCode: Int,
    val url: String
)
