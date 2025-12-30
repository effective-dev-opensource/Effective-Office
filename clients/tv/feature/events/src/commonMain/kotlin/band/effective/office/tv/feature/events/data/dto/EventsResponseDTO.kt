package band.effective.office.tv.feature.events.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventsResponseDTO(
    @SerialName("events")
    val events: List<EventDTO> = emptyList(),
)

@Serializable
data class EventDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("startDateTime")
    val startDateTime: String,
    @SerialName("finishDateTime")
    val finishDateTime: String,
    @SerialName("isOnline")
    val isOnline: Boolean,
    @SerialName("photoUrl")
    val photoUrl: String? = null,
    @SerialName("organizer")
    val organizer: String? = null,
    @SerialName("speakers")
    val speakers: List<String>? = null,
    @SerialName("endRegDate")
    val endRegDate: String? = null,
    @SerialName("location")
    val location: String? = null,
)
