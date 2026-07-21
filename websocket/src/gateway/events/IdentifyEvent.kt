package ktordiscord.gateway.events

import ktordiscord.components.Presence
import ktordiscord.gateway.OPCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(IdentifySerializer::class)
class IdentifyEvent(
    token: String,
    intents: Int
) : Event() {

    @SerialName("d")
    val data = IdentifyData(token, intents)
}

@Serializable
data class IdentifyData(
    val token: String,
    val intents: Int,
    val properties: Properties = Properties(),
    val presence: Presence = Presence()
) {
    @Serializable
    data class Properties(
        val os: String = "Windows",
        val browser: String = "Edge",
        val device: String = "PC"
    )
}

object IdentifySerializer : OutgoingEventSerializer<IdentifyEvent, IdentifyData>(
    "identify", OPCode.IDENTIFY, IdentifyData.serializer(), { it.data }
)