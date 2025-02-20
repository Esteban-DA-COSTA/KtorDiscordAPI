package gateway.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HelloEvent(
    @SerialName("heartbeat_interval")
    val heartbeatInterval: Int
) : Event()