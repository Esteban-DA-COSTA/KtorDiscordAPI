package ktordiscord.gateway.events

import ktordiscord.components.Application
import ktordiscord.components.UnavailableGuild
import ktordiscord.components.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadyEvent(
    override var sequenceId: Int = 0,
    val v: Int,
    val user: User,
    val guilds: List<UnavailableGuild>,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("resume_gateway_url")
    val resumeGatewayUrl: String,
    val application: Application

) : DispatchEvent()